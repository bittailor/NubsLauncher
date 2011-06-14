package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IFilterChain;

public class SuggestTaskTool extends AbstractTool {

   private static Logger LOG = Logger.getLogger(SuggestTaskTool.class.getName());
   
   private IFilterChain filterChain;
   private ListModel suggestListModel;
   private JList suggestList;
   private JPopupMenu suggestPopup;
    
   @Inject
   public SuggestTaskTool(IFilterChain filterChain) {
      super(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK));
      this.filterChain = filterChain;
      suggestListModel = new SuggestListModel(filterChain);
      createUi();       
   }
   
   private void suggestTextChanged() {  
      if(suggestPopup.isVisible()) {
         SwingUtilities.invokeLater(new Runnable() {            
            @Override
            public void run() {
               notiySuggestListeners();
            }
         });
      }
   }
     
   private void createUi() { 
      suggestList = new JList(suggestListModel);
      suggestList.setFocusable(false);
      PopupListDecorator popupListDecorator = new PopupListDecorator(suggestList);
      popupListDecorator.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            enter(e);
            
         }
      });
          
      JScrollPane scrollPane = new JScrollPane(suggestList);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setFocusable(false);
      scrollPane.getHorizontalScrollBar().setFocusable(false);

      suggestPopup = new JPopupMenu();
      suggestPopup.setBorder(BorderFactory.createTitledBorder("Tasks"));
      suggestPopup.add(scrollPane);
      suggestPopup.setFocusable(false);
   }
   
   
   
   @Override
   public void initialize(JTextField tasksTextField, IToolListener listener) {
      super.initialize(tasksTextField, listener);
      getTasksField().getDocument().addDocumentListener(new DocumentListener() {        
         @Override
         public void removeUpdate(DocumentEvent e) {
            suggestTextChanged();      
         }

         @Override
         public void insertUpdate(DocumentEvent e) {
            suggestTextChanged();
         }

         @Override
         public void changedUpdate(DocumentEvent e) {
            suggestTextChanged();
         }
      });
   }

   @Override
   public void activate() {
      suggestList.clearSelection();
      showSuggestPopup();
   }

   @Override
   public void keyUp() {
      changeSelection(suggestList.getSelectedIndex()-1);
   }

   @Override
   public void keyDown() {
      changeSelection(suggestList.getSelectedIndex()+1);
   }
   
   private void changeSelection(int newIndex) {
      int indexInBound = Math.min(Math.max(0,newIndex),suggestListModel.getSize());
      suggestList.setSelectedIndex(indexInBound);
      suggestList.ensureIndexIsVisible(indexInBound);
   }

   @Override
   public void enter(ActionEvent event) {
      closeSuggestPopup();     
      insertCurrentSuggestSelection();
      getListener().finished();
   }

   @Override
   public void escape(ActionEvent event) {
      closeSuggestPopup();
      getListener().finished();
   }
   
   private void showSuggestPopup() {
      // currentState = new SelectRakeTargetTool(this);
      notiySuggestListeners();
      int x = 0;
      try {
         int pos = Math.min(getTasksField().getCaret().getDot(), getTasksField().getCaret().getMark());
         TextUI ui = getTasksField().getUI();
         Rectangle modelToView = ui.modelToView(getTasksField(), pos);
         x = modelToView.x;
      }
      catch (BadLocationException e) {
         LOG.log(Level.WARNING, "Oops, Internal Error", e);
      }
      suggestPopup.setPreferredSize(new Dimension(getTasksField().getWidth()-x, getTasksField().getHeight()*15));
      suggestPopup.show(getTasksField(), x, getTasksField().getHeight());
      getTasksField().requestFocus();
   }
   
   private void notiySuggestListeners() {
      filterChain.setSuggestFilter(getSuggestWord());
   }
   
   private String getSuggestWord() {
      return getTasksField().getText().substring(Math.max(0,getStartIndexOfSuggestWord()),getEndIndexOfSuggestWord());
   }
   
   private int getStartIndexOfSuggestWord() {
      int carret = getEndIndexOfSuggestWord();
      String text = getTasksField().getText();
      for(int i = carret-1 ; i>=0 ; i--){
         if(text.charAt(i)==' ') {
            return i+1;
         }
      }
      return 0;
   }
   
   private int getEndIndexOfSuggestWord() {
      return Math.min(getTasksField().getText().length(), getTasksField().getCaretPosition());
   }
   
   public void insertCurrentSuggestSelection() {
      if (suggestList.isSelectionEmpty()) {
         return;
      }
      
      String selection = suggestList.getSelectedValue().toString(); 
      int offset = getStartIndexOfSuggestWord();
      int length = getEndIndexOfSuggestWord() - offset;
      try {
         Document document = getTasksField().getDocument();
         document.remove(offset,length);
         document.insertString(offset, selection, null);
         offset = offset + selection.length();
         if(!(document.getLength() > offset && document.getText(offset, 1).equals(" "))) {
            document.insertString(offset, " ", null);
         }
      }
      catch (BadLocationException e) {
         LOG.log(Level.WARNING, "Oops, Internal Error", e);
      }
   }
   
   private void closeSuggestPopup() {
      // currentState = new SelectFromCommandHistoryTool(this,commandHistory);
      suggestPopup.setVisible(false);
   }

}
