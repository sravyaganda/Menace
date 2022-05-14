package Layouts;

import java.util.*;

// To make MENACE learn faster, and to reduce the number of matchboxes down to a more manageable
// level, Donald Michie took into account that up to eight different patterns of Noughts and

public class GenerateLayouts {

//    public static void main(String[] args) {
//
//        HashMap<String,String> layoutMap=new HashMap<>();
//        //GenerateLayouts g=new GenerateLayouts();
//        String layout="OXOX.....";
//        GenerateLayouts g=new GenerateLayouts();
//         layoutMap=  g.getMenaceLayouts(layout);
//
//         for(String s:layoutMap.keySet()) {
//
//             String[] arr=layoutMap.get(s).split("#");
//
//             System.out.println(s + "  " + layoutMap.get(s));
//
//         }
//

    //System.out.println(layoutMap.get(".X......O"));


    public GenerateLayouts() {

    }

    public HashMap<String, String> getMenaceLayouts(String layout) {
        try {
            HashMap<String, Boolean> map = GenerateLayouts.getUniqueLayouts(layout);
            HashMap<String, String> layoutmapping = new HashMap<>();

            getSymmetricLayouts(map, layoutmapping);
            //System.out.println( layoutmapping.size());
            MatrixManipulation m = new MatrixManipulation();
            List<String> list = new ArrayList<>();

            for (String s : layoutmapping.keySet()) {
                if (Evaluate.getWinner(m.getMatrixLayout(s)) != "") {
                    list.add(s);
                }
            }

            for (String s : list) {
                layoutmapping.remove(s);
            }

            return layoutmapping;
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }
        return null;
    }

    public static HashMap<String, Boolean> getUniqueLayouts(String layout) {
        try {
            HashMap<String, Boolean> map = new HashMap<String, Boolean>();

            permute(layout, 0, 8, map);

            return map;
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }
        return null;
    }

    //Method to get the possible permutations of the string
    public static void permute(String str, int left, int right, HashMap<String, Boolean> map) {
        try {
            if (left == right) {
                map.put(str, false);
            } else {
                for (int i = left; i <= right; i++) {
                    str = swap(str, left, i);
                    permute(str, left + 1, right, map);
                    str = swap(str, left, i);
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }

    }

    //Method to swap the characters in the string
    public static String swap(String str, int i, int j) {
        try {
            char temp;
            char[] charArray = str.toCharArray();
            temp = charArray[i];
            charArray[i] = charArray[j];
            charArray[j] = temp;
            return String.valueOf(charArray);
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }
return null;
    }

    //Method to get the 8 symmetric layouts for a given layout
    public static void getSymmetricLayouts(HashMap<String, Boolean> map, HashMap<String, String> layoutmapping) {
        try {
            MatrixManipulation m = new MatrixManipulation();
            for (String s : map.keySet()) {
                if (!map.get(s)) {
                    map.put(s, true);
                    layoutmapping.put(s, s + "#" + "SYMMETRIC#" + 0);
                    char[][] layoutMatrix = m.getMatrixLayout(s);
                    List<String> list = getSymmetricStrings(layoutMatrix, s);
                    for (String layoutString : list) {
                        String[] arr = layoutString.split("#");
                        if (!map.get(arr[0])) {
                            map.put(arr[0], true);
                            if (!layoutmapping.containsKey(arr[0])) {
                                layoutmapping.put(arr[0], s + "#" + arr[1] + "#" + arr[2]);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }

    }

    //original board positions is rotated clockwise by a quarter, half,and three-quarter turn
    public static List<String> getSymmetricStrings(char[][] matrix, String s) {
        try {
            MatrixManipulation m = new MatrixManipulation();
            List<String> list = new ArrayList<>();

            char[][] matrix2 = m.getMatrixLayout(s);
            for (int i = 0; i < 3; i++) {
                m.matrixTranspose(matrix);
                m.matrixReflect(matrix);
                int j = i + 1;
                list.add(m.getStringLayout(matrix) + "#SYMMETRIC#" + j);
            }
            list.add(m.getStringLayout(m.matrixVerticalReflect(matrix2)) + "#REFLECT#VERTICAL");
            list.add(m.getStringLayout(m.matrixHorizontalReflect(matrix2)) + "#REFLECT#HORIZONTAL");
            list.add(m.getStringLayout(m.matrixStraightDiagonalReflect(matrix2)) + "#REFLECT#STRAIGHT");
            list.add(m.getStringLayout(m.matrixOppositeDiagonalReflect(matrix2)) + "#REFLECT#OPPOSITE");
            return list;
        } catch (Exception ex) {
            System.out.println("Exception in Class GenerateLayouts: " + ex);
        }
        return null;
    }
}
