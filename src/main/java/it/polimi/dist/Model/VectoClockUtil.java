package it.polimi.dist.Model;

import java.util.ArrayList;

import static java.lang.Math.abs;


public abstract class VectoClockUtil {
    //ArrayList<Integer> vectorClock

    public static boolean equalV(ArrayList<Integer> v1, ArrayList<Integer> v2){
        for (int i=0; i<v1.size();i++){
            if (!v1.get(i).equals(v2.get(i)))
                return false;
        }
        return true;
    }

    public static boolean outOfSequence(ArrayList<Integer> vMessage, ArrayList<Integer> vClock, int serverNumber){
        if(vMessage.get(serverNumber)>(vClock.get(serverNumber)+1))
            return true;
        return false;
    }
    /*
    index[0] -> serverNumber
    index[1] -> timestamp
     */
    public static int[] missedMessage (ArrayList<Integer> vMessage, ArrayList<Integer> vClock){
        int index[] = new int[2];
        //todo
        return index;
    }

    /*
    it returns:
     0 if v1 and v2 are not comparable (or IF THEY ARE EQUAL-> control this before)
    +1 if v1 > v2
    -1 if v1 < v2
     */
    public static int compare(ArrayList<Integer> v1, ArrayList<Integer> v2){
       // boolean comparable=true;
        int greater = 0;
        for (int i = 0; i < v1.size(); i++) {
            if (v1.get(i) < v2.get(i)) {
                if (greater == 0) {
                    greater = -1;
                }else{
                    if (greater == 1) {
                        //comparable=false;
                        return 0;
                    }
                    greater = -1;
                }
            }else if(v1.get(i) > v2.get(i)){
                if (greater == 0) {
                    greater = +1;
                }else{
                    if (greater == -1) {
                        //comparable=false;
                        return 0;
                    }
                    greater = +1;
                }
            }
        }
        //if (comparable)
            return greater;
        //else
        //    return 0;
    }

    public static ArrayList<Integer> addOne(Logic logic) {
        ArrayList<Integer> res;
        int temp;
        synchronized (logic) {
            res = logic.vectorClock;
            temp = res.get(logic.serverNumber) + 1;
            res.set(logic.serverNumber, temp);
            logic.vectorClock=res;
        }
        return res;
    }


    public static int hammingDist(ArrayList<Integer> v1, ArrayList<Integer> v2){
        int count=0;
        for (int i = 0; i < v1.size(); i++){
            if (!v1.get(i).equals(v2.get(i)))
                count=count+abs(v1.get(i)-v2.get(i));
        }
        return count;
    }

}
