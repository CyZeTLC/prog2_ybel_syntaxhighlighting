package highlighting.presets;

public final class Texts {
    public static final String START_TEXT =
            """
            package controller;

            import com.badlogic.gdx.Game;
            import com.badlogic.gdx.graphics.g2d.SpriteBatch;

            /* ApplicationListener that delegates to the MainGameController. Just some setup. */
            public class LibgdxSetup extends Game {
                private final MainController mc;
                private final String FOO = "wuppie fluppie";

                /**
                 * The batch is "necessary" to draw ALL the stuff. Every object that uses draw need to know
                 * the batch.
                 */
                private SpriteBatch batch;

                // This batch is used to draw the HUD /*elements*/ on it.
                private SpriteBatch hudBatch;

                /**
                 * "ApplicationListener" that delegates to the "MainGameController". Just some setup.
                 */
                public LibgdxSetup(MainController mc) {
                    this.mc = mc;
                }

                @Over-ride 'someText'
                public void create() {
                    // new ...
                    char ch = new Character('a');
                    return null;
                }
            }
            """;
}
