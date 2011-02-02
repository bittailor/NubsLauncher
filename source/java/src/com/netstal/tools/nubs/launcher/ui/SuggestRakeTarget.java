//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class SuggestRakeTarget implements IRakeTextFieldTool {

   private List<ISuggestFieldListener> suggestListeners = new ArrayList<ISuggestFieldListener>();
   
   private JTextField suggestTextField;
   private ListModel suggestListModel;
   private JList suggestList;
   private JPopupMenu suggestPopup;
    
   public SuggestRakeTarget(JTextField suggestTextField, ListModel suggestListModel) {
      this.suggestTextField = suggestTextField;
      this.suggestListModel = suggestListModel;
      createUi();
      
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
   
   public void addSuggestListener(ISuggestFieldListener listener) {
      suggestListeners.add(listener);
   }
   
   public void removeSuggestListener(ISuggestFieldListener listener) {
      suggestListeners.remove(listener);
   }
   
   private void createUi() { 
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
   
   @Override
   public void activate() {
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
      insertCurrentSuggestSelection();
   }

   @Override
   public void escape(ActionEvent event) {
      closeSuggestPopup();
   }
   
   @Override
   public int getKeyCode() {
      return KeyEvent.VK_SPACE;
   }

   @Override
   public int getModifiers() {
      return InputEvent.CTRL_MASK;
   }
   
   private void showSuggestPopup() {
      // currentState = new SelectRakeTargetTool(this);
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
   
   private void notiySuggestListeners() {
      for ( ISuggestFieldListener listener : suggestListeners) {
         System.out.println(getSuggestWord());
         listener.changed(getSuggestWord() );
      }
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
   
   public void insertCurrentSuggestSelection() {
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
   }
   
   private void closeSuggestPopup() {
      // currentState = new SelectFromCommandHistoryTool(this,commandHistory);
      suggestPopup.setVisible(false);
   }

}
