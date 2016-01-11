package shadertest;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;
import java.util.Arrays;
import java.util.Random;

public class ShaderTest {
    public static final int  SAMPLE_COUNT = 25;
    public static final long SEQUENCE_COUNT = 500000;
    public static final long SEQUENCE_DURATION_MSEC = 200;
    
    public static int wrap(int num, int wrap){
        return num % wrap;
    }
    
    public static int hash(int x) {
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = ((x >> 16) ^ x);
        return x;
    }
    
    public static void main(String[] args) throws InterruptedException {
        Terminal terminal = TerminalFacade.createSwingTerminal();
        
        terminal.enterPrivateMode();
        terminal.setCursorVisible(false);
        
        while(true){
            long time_msec = System.currentTimeMillis();
            int sequence = (int)((time_msec / SEQUENCE_DURATION_MSEC) % SEQUENCE_COUNT);
            
            Random rand = new Random();
            
            for(int i = 0; i < terminal.getTerminalSize().getRows(); ++i){
                for(int j = 0; j < terminal.getTerminalSize().getColumns(); ++j){
                    int seed = hash(Arrays.hashCode(new int[]{j, i}));
                    float avg_val = 0.f;
                    
                    for(int k = 0; k < SAMPLE_COUNT; k++){
                        rand.setSeed(seed + (sequence - k % SEQUENCE_COUNT) * 24232);
                        float cur_sample = rand.nextFloat();
                        avg_val = (cur_sample / (float)SAMPLE_COUNT) + ((float)SAMPLE_COUNT - 1.f) / (float)SAMPLE_COUNT * avg_val ;
                    }

                    terminal.moveCursor(j, i);
                    terminal.applyBackgroundColor((int)(255.f * avg_val), (int)(255.f * avg_val), (int)(255.f * avg_val));
                    terminal.putCharacter(' ');
                    
                }
            }
            
            terminal.flush();
            Thread.sleep(100);
        }
    }
}
