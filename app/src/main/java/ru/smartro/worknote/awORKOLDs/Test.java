package ru.smartro.worknote.awORKOLDs;

// Test.java
import android.util.Log;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Comparator;

//игра попросила Unconfirmed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
public class Test {
    public void main() {
        Comparator<String> comparator = new StringLengthComparator();
        PriorityQueue<String> queue = new PriorityQueue<String>(10, comparator);
        queue.add("ashort");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf("ashort"));
//        queue.add("аФшort");
        queue.add("bashort");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf(1111));

        queue.add("fas!hort");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf(1111));

        queue.add("cvery long indeed");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf(1111));

        queue.add("dmedium не беси меня)");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf(1111));

        queue.add("eЯadov бес и Я на)");
        Log.i("GALya[Nn,", "queue.add.after" + String.valueOf(1111));

        queue.add("Яма");
        queue.add("МогЯadov бес и Я на)");
        queue.add("СмолeЯadov бес и Я на)");
        while (queue.size() != 0) {
            String element = queue.remove();
            Log.i("GALya[Nn,element=", element);
        }
    }

    public class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String x, String y) {
            // Assume neither string is null. Real code should
            // probably be more robust
            // You could also just return x.length() - y.length(),
            // which would be more efficient.
            char firstSymbolX = x.charAt(0);
            char firstSymbolY = y.charAt(0);
            if (firstSymbolX > firstSymbolY) {
                return 1;
            }
            if (firstSymbolX < firstSymbolY) {
                return -1;
            }
            return 0;
//            Log.i("GALya[Nn,firstSymbolX=",  String.valueOf(Integer.valueOf(firstSymbolX)));
//            Log.i("GALya[Nn,", "firstSymbolX=" + String.valueOf(firstSymbolX));
//
//            Log.i("GALya[Nn,firstSymbolY=", String.valueOf(firstSymbolY));
//            Log.i("GALya[Nn,", "firstSymbolY=" + String.valueOf(firstSymbolY));


//            return firstSymbolY;
        }
    }
}

// StringLengthComparator.java

