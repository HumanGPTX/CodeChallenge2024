package humangpt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Main {

    static int W;
    static int H;
    static int GN;
    static int SM;
    static int TL;
    static List<GoldenPoint> goldenPoints;
    static List<SilverPoint> silverPoints;
    static List<Tile> tiles;
    static List<Tile> chosenTiles;

    private static final Consumer<Scanner> HOW_TO_READ = scanner -> {
        // Read the first line with grid dimensions and point/tile counts
        String[] firstLineTokens = scanner.nextLine().split(" ");
        W = Integer.parseInt(firstLineTokens[0].substring(1));
        H = Integer.parseInt(firstLineTokens[1]);
        GN = Integer.parseInt(firstLineTokens[2]);
        SM = Integer.parseInt(firstLineTokens[3]);
        TL = Integer.parseInt(firstLineTokens[4]);

        // Read Golden Points
        goldenPoints = new ArrayList<>();
        for (int i = 0; i < GN; i++) {
            String[] goldenPointTokens = scanner.nextLine().split(" ");
            int GX = Integer.parseInt(goldenPointTokens[0]);
            int GY = Integer.parseInt(goldenPointTokens[1]);
            goldenPoints.add(new GoldenPoint(GX, GY));
        }

        // Read Silver Points
        silverPoints = new ArrayList<>();
        for (int j = 0; j < SM; j++) {
            String[] silverPointTokens = scanner.nextLine().split(" ");
            int SX = Integer.parseInt(silverPointTokens[0]);
            int SY = Integer.parseInt(silverPointTokens[1]);
            int SSC = Integer.parseInt(silverPointTokens[2]);
            silverPoints.add(new SilverPoint(SX, SY, SSC));
        }

        // Read Tiles
        tiles = new ArrayList<>();
        for (int k = 0; k < TL; k++) {
            String[] tileTokens = scanner.nextLine().split(" ");
            String TID = tileTokens[0];
            int TC = Integer.parseInt(tileTokens[1]);
            int TN = Integer.parseInt(tileTokens[2]);
            tiles.add(new Tile(TID, TC, TN));
        }
    };

    public static void main(String[] arguments) throws Exception {
        final Args args = IOUtils.checkArgs(arguments);
        IOUtils.readInput(args.getInputFile(), HOW_TO_READ);

        System.out.println("W: " + W);
        System.out.println("H: " + H);
        System.out.println("GN: " + GN);
        System.out.println("SM: " + SM);
        System.out.println("TL: " + TL);
        goldenPoints.forEach(System.out::println);
        silverPoints.forEach(System.out::println);
        tiles.forEach(System.out::println);
        List<Tile> chosenTiles = new ArrayList<>();

        List<Cell> chosenPath = computeCompletePath(goldenPoints);
        chosenPath.forEach(System.out::println);

        Iterator<Tile> tilesIterator = chosenTiles.iterator();
        Iterator<Cell> pathIterator = chosenPath.iterator();
        List<String> formattedPlacementLines = new ArrayList<>();
        while (tilesIterator.hasNext() && pathIterator.hasNext()) {
            Tile tile = tilesIterator.next();
            Cell cell = pathIterator.next();
            formattedPlacementLines.add(String.format("%s %d %d\n", tile.TID, cell.getGX(), cell.getGY()));
        }

        List<String> outputLines = formattedPlacementLines;
        IOUtils.writeOutput(args.getOutputFile(), outputLines);
    }

    static class Cell {

        int X;
        int Y;

        public Cell(int GX, int GY) {
            this.X = GX;
            this.Y = GY;
        }

        // Getters for GX and GY
        public int getGX() {
            return X;
        }

        public int getGY() {
            return Y;
        }

        @Override
        public String toString() {
            return "Cell(X=" + X + ", Y=" + Y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            Cell other = (Cell) obj;
            return this.X == other.X && this.Y == other.Y;
        }
    }

    static class GoldenPoint extends Cell {

        public GoldenPoint(int GX, int GY) {
            super(GX, GY);
        }
        @Override
        public String toString() {
            return "GoldenPoint(GX=" + X + ", GY=" + Y + ")";
        }
    }

    static class SilverPoint {
        int SX;
        int SY;
        int SSC;

        public SilverPoint(int SX, int SY, int SSC) {
            this.SX = SX;
            this.SY = SY;
            this.SSC = SSC;
        }

        // Getters for SX, SY, and SSC
        public int getSX() {
            return SX;
        }

        public int getSY() {
            return SY;
        }

        public int getSSC() {
            return SSC;
        }
        @Override
        public String toString() {
            return "SilverPoint(SX=" + SX + ", SY=" + SY + ", SSC=" + SSC + ")";
        }
    }

    static class Tile {
        String TID;
        int TC;
        int TN;

        public Tile(String TID, int TC, int TN) {
            this.TID = TID;
            this.TC = TC;
            this.TN = TN;
        }

        // Getters for TID, TC, and TN
        public String getTID() {
            return TID;
        }

        public int getTC() {
            return TC;
        }

        public int getTN() {
            return TN;
        }
        @Override
        public String toString() {
            return "Tile(TID=" + TID + ", TC=" + TC + ", TN=" + TN + ")";
        }
    }

    public static int calculateScore(List<Cell> path, List<Tile> tiles) {

        // Calculate minimum path score between all Golden Point pairs
        int minPathScore = calculateMinPathScore(path);

        // Calculate total Tile cost (subtract once for each Tile)
        int totalTileCost = tiles.stream().mapToInt(Tile::getTC).sum();

        // Combine minimum path score and total tile cost
        int score = minPathScore - totalTileCost;

        // Ensure final score is not less than 0
        return Math.max(score, 0);
    }

    // Function to calculate minimum path score between all Golden Point pairs (implementation details omitted)
    private static int calculateMinPathScore(List<Cell> path) {
        int score = 0;
        for (Cell cell : path) {
            List<SilverPoint> equals = silverPoints.stream()
                    .filter(p -> p.getSX() == cell.getGX() && p.getSY() == cell.getGY())
                    .collect(Collectors.toList());
            if (equals.size() == 1)
                score += equals.get(0).getSSC();
        }
        return score;
    }

    static GoldenPoint computeClosestGoldenPoint(Iterator<GoldenPoint> goldenPoints, GoldenPoint startingPoint){
        GoldenPoint closest = new GoldenPoint(1000, 1000);
        double minDistance = 100000;

        while (goldenPoints.hasNext()) {
            GoldenPoint next = goldenPoints.next();
            closest = next;
            double distance = Math.sqrt(Math.pow(startingPoint.getGX() - next.getGX(), 2) + Math.pow(startingPoint.getGY() - next.getGY(), 2));
            if (distance <  minDistance){
                closest = next;
                minDistance = distance;
            }
        }

        return closest;
    }

    static List<Cell> computePathBetweenTwoPoints(Cell startingP, Cell endingPoint){
        List<Cell> path = new ArrayList<Cell>();
        Cell startingPoint = startingP;

        path.add(startingPoint);
        if (startingPoint.X > endingPoint.X) {
            while (startingPoint.X > endingPoint.X) {
                Cell cell = new Cell(startingPoint.X-1,startingPoint.Y);
                path.add(cell);
                startingPoint = cell;
            }
        } else {
            while (startingPoint.X < endingPoint.X) {
                Cell cell = new Cell(startingPoint.X+1,startingPoint.Y);
                path.add(cell);
                startingPoint = cell;
            }
        }

        if (startingPoint.Y > endingPoint.Y) {
            while (startingPoint.Y > endingPoint.Y) {
                Cell cell = new Cell(startingPoint.X,startingPoint.Y-1);
                path.add(cell);
                startingPoint = cell;
            }
        } else {
            while (startingPoint.Y < endingPoint.Y) {
                Cell cell = new Cell(startingPoint.X,startingPoint.Y+1);
                path.add(cell);
                startingPoint = cell;
            }
        }

        path.add(endingPoint);
        return path;
    }

    static List<Cell> computeCompletePath(List<GoldenPoint> goldenPoints){
        List<Cell> completePath = new ArrayList<>();

        List<GoldenPoint> copy = new ArrayList<>(goldenPoints);
        Iterator<GoldenPoint> iterator = copy.iterator();

        GoldenPoint nextInPath = iterator.next();
        GoldenPoint first = nextInPath;
        GoldenPoint next = null;
        while(iterator.hasNext()){
            next = nextInPath;
            System.out.println("next " + next);

            copy.remove(next);
            iterator = copy.iterator();
            if (!iterator.hasNext()) {
                break;
            }
            nextInPath = computeClosestGoldenPoint(copy.iterator(), next);
            System.out.println("nextInPath " + nextInPath);

            List<Cell> path = computePathBetweenTwoPoints(next, nextInPath);
            System.out.println("path");
            path.forEach(System.out::println);
            completePath.addAll(path);
        }

        // Compute Last path
        List<GoldenPoint> firstList = new ArrayList<Main.GoldenPoint>();
        firstList.add(first);
        nextInPath = computeClosestGoldenPoint(firstList.iterator(), next);
        System.out.println("nextInPath " + nextInPath);

        List<Cell> path = computePathBetweenTwoPoints(next, nextInPath);
        System.out.println("path");
        path.forEach(System.out::println);
        completePath.addAll(path);
        return completePath;
    }

    // NON L'ABBIAMO SCRITTA PER IL ROTTO DELLA CUFFIA (NON � VERO)
    // Dimostrazione lasciata al lettore
    static Tile chooseTile(Cell start, Cell end) {
        return null;
    }

}