package TextEdit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;


public class TextProcessor {

    public static void LineUpdates(JTextArea area, List<List<String>> undoManager) {
        String[] lines = area.getText().split("\n");

        for (int i = 0; i < lines.length; i++) {
            // line is new
            if (undoManager.isEmpty() || undoManager.size() <= i) {
                List<String> currLsit = new ArrayList<String>();
                currLsit.add(lines[i]);
                undoManager.add(currLsit);
            }
            //if line exists
            else {
                List<String> currLineInUM = undoManager.get(i);
                String currState = currLineInUM.get(currLineInUM.size() - 1);
                if (!currState.equals(lines[i])) {
                    currLineInUM.add(lines[i]);
                }
            }
        }
    }

    public static void Print(List<List<String>> undoManager) {
        System.out.println("#AreaProcessor.Print");
        //printing
        for (int i = 0; i < undoManager.size(); i++) {

            System.out.print("Line: " + (i + 1) + " | ");
            List<String> currLineinUM = undoManager.get(i);
            for (int j = currLineinUM.size() - 1; j >= 0; --j) {
                System.out.print(" state :" + j + " " + currLineinUM.get(j));
            }
            System.out.println();
        }
    }

    public static void Undo(List<List<String>> undoManager, TextEditTable dataTable, JTextArea area) {

        // insert/extract the selection information into ArrayList
        ArrayList<Object[]> pairList = new ArrayList<Object[]>();
        DefaultTableModel model = dataTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 5).equals(true)) {
                // line number, state number, row number to delete
                pairList.add(new Object[]{model.getValueAt(i, 1), model.getValueAt(i, 2), (Integer) i});
            }
        }


        // delete unnecessary entries
        for (int i = pairList.size() - 1; i >= 0; --i) {
            Object[] lineStatePair = pairList.get(i);
            int lineNum = (int) lineStatePair[0];
            int stateNum = (int) lineStatePair[1];
            int deleteRowNum = (int) lineStatePair[2];
            int latestState = (int) undoManager.get(lineNum).size() - 1;
            for (int j = latestState; j >= stateNum; --j) {
                undoManager.get(lineNum).remove(j);
                model.removeRow(deleteRowNum);  //delete one line from the table
            }
            // decrease line number
            if (stateNum == 0) {
                undoManager.remove(lineNum);
                for (int k = 0; k < model.getRowCount(); k++) {
                    int preValue = (Integer) model.getValueAt(k, 1);
                    if (preValue > lineNum) {
                        model.setValueAt(preValue - 1, k, 1);
                    }

                }
            }
        }

        // update JTextarea with the lines with the latest texts
        ArrayList<String> s = new ArrayList<>();
        for (int i = 0; i < undoManager.size(); i++) {
            s.add(undoManager.get(i).get(undoManager.get(i).size() - 1));
        }

        String[] lineSeparateArray = s.toArray(new String[s.size()]);
        String joined = String.join("\n", lineSeparateArray);
        area.setText(joined);
    }

    public static void Forget(List<List<String>> undoManager, TextEditTable dataTable) {

        // insert/extract the selection information into ArrayList
        ArrayList<Object[]> pairList = new ArrayList<Object[]>();
        DefaultTableModel model = dataTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 5).equals(true)) {
                // line number, state number, row number to delete
                pairList.add(new Object[]{model.getValueAt(i, 1), model.getValueAt(i, 2), (Integer) i});
            }
        }

        // delete unnecessary entries
        for (int i = pairList.size() - 1; i >= 0; --i) {
            Object[] lineStatePair = pairList.get(i);
            int lineNum = (int) lineStatePair[0];
            int stateNum = (int) lineStatePair[1];
            int deleteRowNum = (int) lineStatePair[2];
            int oldestState = 0;
            int deleteCounter = 0;

            //St0 St1 St2
            for (int j = oldestState; j < stateNum; j++) {
                undoManager.get(lineNum).remove(0);
                model.removeRow(--deleteRowNum);  //delete one line from the table
                deleteCounter++;
            }
            // decrease state number
            for (int k = 0; k < model.getRowCount(); k++) {
                int preLineValue = (Integer) model.getValueAt(k, 1);
                int preStateValue = (Integer) model.getValueAt(k, 2);
                if (preLineValue == lineNum) {
                    model.setValueAt(preStateValue - deleteCounter, k, 2);
                }
            }

        }
    }

    public static void SelectLatest(TextEditTable dataTable) {
        DefaultTableModel model = dataTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            dataTable.setValueAt(true, i, 5);
        }
    }
}
