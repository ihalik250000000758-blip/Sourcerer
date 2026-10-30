package sourcerer.model;

import java.util.List;

/** One level on the map: a Python concept, a short lesson card and a list of puzzles. */
public class Level {

    private final int id;
    private final String title;
    private final String blurb;
    private final String sampleCode;
    private final String sampleOutput;
    private final List<Puzzle> puzzles;
    private final boolean boss;

    public Level(int id, String title, String blurb, String sampleCode, String sampleOutput, List<Puzzle> puzzles) {
        this(id, title, blurb, sampleCode, sampleOutput, puzzles, false);
    }

    public Level(int id, String title, String blurb, String sampleCode, String sampleOutput,
                 List<Puzzle> puzzles, boolean boss) {
        this.id = id;
        this.title = title;
        this.blurb = blurb;
        this.sampleCode = sampleCode;
        this.sampleOutput = sampleOutput;
        this.puzzles = puzzles;
        this.boss = boss;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBlurb() { return blurb; }
    public String getSampleCode() { return sampleCode; }
    public String getSampleOutput() { return sampleOutput; }
    public List<Puzzle> getPuzzles() { return puzzles; }
    /** True for every tenth campaign stage, which includes extra challenge puzzles. */
    public boolean isBoss() { return boss; }

    // Each level has its own unique enemy, defined by the switch statement below.
       /**
     * Spawns the correct enemy for this specific level.
     */
    public Bug createBug() {
        if (boss) return new BossBug(id);
        
        // The tutorial is id 0, but we want it to use the Level 1 bat
        int enemyId = Math.max(1, id); 
        
        switch (enemyId) {
            case 1:  return new GreyBat();
            case 2:  return new Blobfish();
            case 3:  return new ShadowFox();
            case 4:  return new LeafSpider();
            case 5:  return new TopHatTiger();
            case 6:  return new PurpleBat();
            case 7:  return new DemonSpider();
            default: return new GreyBat();
        }
    }
    
}
