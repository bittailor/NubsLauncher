package com.netstal.tools.nubs.launcher.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class SuggestField extends JPanel {
   
   private static final long serialVersionUID = 1L;
   private static final String KEY_DOWN = "Key Down";
   private static final String KEY_UP = "Key Up";
   private static final String SHOW_SUGGEST_POPUP = "ShowSuggestPopup";
   private static final String CLOSE_SUGGEST_POPUP = "CloseSuggestPopup";
   
   private JTextField suggestTextField;
   private ListModel suggestListModel;
   private JList suggestList;
   private JPopupMenu suggestPopup;
   private List<ISuggestFieldListener> suggestListeners = new ArrayList<ISuggestFieldListener>();
   private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
   
   public SuggestField(ListModel suggestListModel) {
     
      this.suggestListModel = suggestListModel;
      createUi();
      createActions();
   }
   
   public void addSuggestListener(ISuggestFieldListener listener) {
      suggestListeners.add(listener);
   }
   
   public void removeSuggestListener(ISuggestFieldListener listener) {
      suggestListeners.remove(listener);
   }
   
   public void addActionListener(ActionListener listener) {
      actionListeners.add(listener);
   }
   
   public void removeActionListener(ActionListener listener) {
      actionListeners.remove(listener);
   }
   
   public String getText() {
      return suggestTextField.getText();
   }
   
   private void createUi() {
      suggestTextField = new JTextField();
      suggestTextField.setPreferredSize(new Dimension(1000,suggestTextField.getPreferredSize().height));
      add(suggestTextField);  
     
      suggestList = new JList(suggestListModel);
      suggestList.setFocusable(false);
      
      JScrollPane scrollPane = new JScrollPane(suggestList);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setFocusable(false);
      scrollPane.getHorizontalScrollBar().setFocusable(false);

      suggestPopup = new JPopupMenu();
      suggestPopup.setBorder(BorderFactory.createLineBorder(Color.black));
      suggestPopup.add(scrollPane);
      suggestPopup.setFocusable(false);
   }

   private void createActions() {
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK),SHOW_SUGGEST_POPUP);
      suggestTextField.getActionMap().put(SHOW_SUGGEST_POPUP,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            showSuggestPopup();
         }     
      });
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),CLOSE_SUGGEST_POPUP);
      suggestTextField.getActionMap().put(CLOSE_SUGGEST_POPUP,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            closeSuggestPopup();
         }     
      });
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),KEY_DOWN);
      suggestTextField.getActionMap().put(KEY_DOWN,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            changeSelection(suggestList.getSelectedIndex()+1);
         }     
      });
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),KEY_UP);
      suggestTextField.getActionMap().put(KEY_UP,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            changeSelection(suggestList.getSelectedIndex()-1);
         }     
      });
      
      suggestTextField.getDocument().addDocumentListener(new DocumentListener() {        
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
      
      suggestTextField.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent event) {
            if (suggestPopup.isVisible()) {
               closeSuggestPopup();

               if (suggestList.isSelectionEmpty()) {
                  return;
               }
               
               String selection = suggestList.getSelectedValue().toString(); 
               int offset = getStartIndexOfSuggestWord();
               int length = getEndIndexOfSuggestWord() - offset;
               try {
                  Document document = suggestTextField.getDocument();
                  document.remove(offset,length);
                  document.insertString(offset, selection, null);
                  offset = offset + selection.length();
                  if(!(document.getLength() > offset && document.getText(offset, 1).equals(" "))) {
                     document.insertString(offset, " ", null);
                  }
               }
               catch (BadLocationException e) {
                  e.printStackTrace();
               }
            } else {
               notifyActionListeners(event);
            }
         }
      });
   }
   
   private void changeSelection(int newIndex) {
      int indexInBound = Math.min(Math.max(0,newIndex),suggestListModel.getSize());
      suggestList.setSelectedIndex(indexInBound);
      suggestList.ensureIndexIsVisible(indexInBound);
   }
   
   private String getSuggestWord() {
      return suggestTextField.getText().substring(Math.max(0,getStartIndexOfSuggestWord()),getEndIndexOfSuggestWord());
   }
   
   private int getStartIndexOfSuggestWord() {
      int carret = getEndIndexOfSuggestWord();
      String text = suggestTextField.getText();
      for(int i = carret-1 ; i>=0 ; i--){
         if(text.charAt(i)==' ') {
            return i+1;
         }
      }
      return 0;
   }
   
   private int getEndIndexOfSuggestWord() {
      return Math.min(suggestTextField.getText().length(), suggestTextField.getCaretPosition());
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
 
   private void notiySuggestListeners() {
      for ( ISuggestFieldListener listener : suggestListeners) {
         System.out.println(getSuggestWord());
         listener.changed(getSuggestWord() );
      }
   }
   
   private void notifyActionListeners(ActionEvent event) {
      for (ActionListener listener : actionListeners) {
         listener.actionPerformed(event);
      }
   }

   private void showSuggestPopup() {
      notiySuggestListeners();
      int x = 0;
      try {
         int pos = Math.min(suggestTextField.getCaret().getDot(), suggestTextField.getCaret().getMark());
         x = suggestTextField.getUI().modelToView(suggestTextField, pos).x;
      }
      catch (BadLocationException e) {
         // this should never happen!!!
         e.printStackTrace();
      }
      suggestPopup.show(suggestTextField, x, suggestTextField.getHeight());
      suggestTextField.requestFocus();
   }
   
   private void closeSuggestPopup() {
      suggestPopup.setVisible(false);
   }
   
}
