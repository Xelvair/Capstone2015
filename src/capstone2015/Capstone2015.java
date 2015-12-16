package capstone2015;

import capstone2015.AppState.*;

public class Capstone2015 {
    public static void main(String[] args) throws Exception {
        AppStateManager asm = new AppStateManager();
        
        asm.pushState(new AppState(){

            @Override
            protected void onTick(double timeDelta) {
                if(this.isFocus()){
                    System.out.println("State #1 updating!");
                }
                
                System.out.println("State #1 drawing!");
                this.kill();
            }

            @Override
            protected void onEvent(AppStateEvent event) {
                System.out.println("State #1 event: " + event.toString());
            }

        });
        
        asm.pushState(new AppState(){

            @Override
            protected void onTick(double timeDelta) {
                if(this.isFocus()){
                    System.out.println("State #2 updating!");
                }
                
                System.out.println("State #2 drawing!");
            }

            @Override
            protected void onEvent(AppStateEvent event) {
                System.out.println("State #2 event: " + event.toString());
            }

        });
        
        while(!asm.isEmpty()){
            asm.tick(0.f);
            System.out.println();
            Thread.sleep(1000);
        }
    }
}
