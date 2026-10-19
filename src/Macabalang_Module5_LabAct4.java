import java.util.*;

public class Macabalang_Module5_LabAct4 {

    static class Node {
        String word;
        int val;
        int height = 1;
        Node left, right;

        Node(String word) {
            this.word = word;
            this.val = wordValue(word);
        }

        String full() { return word; }
    }

    static int wordValue(String w) {
        int sum = 0;
        for (char ch : w.toCharArray()) sum += ch;
        return sum;
    }

    static int compare(Node a, Node b) {
        if (a.val != b.val) return Integer.compare(a.val, b.val);
        return a.word.compareTo(b.word);
    }

    static Node arrayToTree(Node[] a, int i) {
        if (i >= a.length) return null;
        Node n = new Node(a[i].word);
        n.left = arrayToTree(a, 2 * i + 1);
        n.right = arrayToTree(a, 2 * i + 2);
        return n;
    }

    static int h(Node n) { return n == null ? 0 : n.height; }

    static void update(Node n) { n.height = 1 + Math.max(h(n.left), h(n.right)); }

    static int balanceFactor(Node n) { return n == null ? 0 : h(n.left) - h(n.right); }

    static Node rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        x.right = y;
        update(y);
        update(x);
        return x;
    }

    static Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        update(x);
        update(y);
        return y;
    }

    static Node avlInsert(Node root, Node n) {
        if (root == null) return n;

        int c = compare(n, root);
        if (c < 0) root.left = avlInsert(root.left, n);
        else if (c > 0) root.right = avlInsert(root.right, n);
        else return root;

        update(root);
        int bf = balanceFactor(root);

        if (bf > 1) {
            if (compare(n, root.left) < 0) {
                return rotateRight(root);
            } else {
                root.left = rotateLeft(root.left);
                return rotateRight(root);
            }
        }
        if (bf < -1) {
            if (compare(n, root.right) > 0) {
                return rotateLeft(root);
            } else {
                root.right = rotateRight(root.right);
                return rotateLeft(root);
            }
        }
        return root;
    }

    static int depth(Node n) {
        return n == null ? 0 : 1 + Math.max(depth(n.left), depth(n.right));
    }

    static int gap(int d, int h) { return 1 << (h - 2 - d); }

    static void printTree(Node root) {
        int h = depth(root);
        int[] row = new int[h];
        for (int d = 0; d < h - 1; d++) row[d + 1] = row[d] + 1 + gap(d, h);
        int rows = row[h - 1] + 1;
        int width = (1 << (h - 1)) * 6 + 2;

        char[][] c = new char[rows][width];
        for (char[] r : c) Arrays.fill(r, ' ');

        place(c, root, 0, 0, h, row);
        for (char[] r : c) System.out.println(new String(r).replaceAll("\\s+$", ""));
    }

    static void place(char[][] c, Node n, int d, int k, int h, int[] row) {
        if (n == null) return;
        int col = (2 * k + 1) * (1 << (h - 1 - d)) * 3;
        int start = col - n.word.length() / 2;
        for (int i = 0; i < n.word.length(); i++) c[row[d]][start + i] = n.word.charAt(i);

        if (d == h - 1) return;
        for (int side = 0; side < 2; side++) {
            Node child = side == 0 ? n.left : n.right;
            if (child == null) continue;
            int ck = 2 * k + side;
            int childCol = (2 * ck + 1) * (1 << (h - 2 - d)) * 3;
            int dist = Math.abs(childCol - col);
            int g = gap(d, h);
            int sign = side == 0 ? -1 : 1;
            for (int j = 1; j <= g; j++) {
                int cc = col + sign * Math.round((float) j * dist / (g + 1));
                c[row[d] + j][cc] = side == 0 ? '/' : '\\';
            }
            place(c, child, d + 1, ck, h, row);
        }
    }

    static void pre(Node n, StringBuilder sb) {
        if (n == null) return;
        sb.append(n.full()).append(" ");
        pre(n.left, sb);
        pre(n.right, sb);
    }

    static void post(Node n, StringBuilder sb) {
        if (n == null) return;
        post(n.left, sb);
        post(n.right, sb);
        sb.append(n.full()).append(" ");
    }

    static void in(Node n, StringBuilder sb) {
        if (n == null) return;
        in(n.left, sb);
        sb.append(n.full()).append(" ");
        in(n.right, sb);
    }

    static void showTree(String title, Node root) {
        System.out.println("\n" + title + " (height " + depth(root) + ")");
        printTree(root);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String sentence = "The quick brown fox jumps over the lazy dog.";
        String[] words = sentence.replace(".", "").split("\\s+");

        Node[] input = new Node[words.length];
        Node avl = null;
        for (int i = 0; i < words.length; i++) {
            input[i] = new Node(words[i]);
            avl = avlInsert(avl, new Node(words[i]));
        }

        showTree("Original Tree", arrayToTree(input, 0));

        while (true) {
            System.out.println("\n1. Tree");
            System.out.println("2. Pre");
            System.out.println("3. Post");
            System.out.println("4. In");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.");
                continue;
            }

            StringBuilder sb = new StringBuilder();
            if (choice == 1) {
                showTree("AVL Tree", avl);
            } else if (choice == 2) {
                pre(avl, sb);
                System.out.println("\nPre:  " + sb);
            } else if (choice == 3) {
                post(avl, sb);
                System.out.println("\nPost: " + sb);
            } else if (choice == 4) {
                in(avl, sb);
                System.out.println("\nIn:   " + sb);
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        sc.close();
    }
}

