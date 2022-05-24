package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double CONCERT_A = 440.0;
    private static final int G = 37;
    GuitarString[] gstr = new GuitarString[G];

    public GuitarHero() {
        for (int i = 0; i < G; i += 1) {
            double freq = CONCERT_A * Math.pow(2, (i - 24) / 12);
            gstr[i] = new GuitarString(freq);
        }
    }

    public static void main(String[] args) {
        GuitarHero gh = new GuitarHero();

        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = gh.keyboard.indexOf(key);
                if (index >= 0) {
                    gh.gstr[index].pluck();
                }
            }

            double sample = 0.0;
            for (GuitarString gs : gh.gstr) {
                sample += gs.sample();
            }

            StdAudio.play(sample);

            for (GuitarString gs : gh.gstr) {
                gs.tic();
            }
        }
    }
}
