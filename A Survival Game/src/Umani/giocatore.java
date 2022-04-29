package Umani;

import a.survival.game.Pannello;
import a.survival.game.Tastiera;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Denis
 */
public class giocatore extends umani{
    Pannello p;
    Tastiera t;
    
    public final int schermoX;
    public final int schermoY;
    
    public giocatore(Pannello p,Tastiera t) {
        this.p=p;
        this.t=t;
        
        schermoX=p.FinestraA /2-(p.FinalAP/2);
        schermoY=p.FinestraL/2-(p.FinalAP/2);
        
        setBasi();
        getImmagineG();
    }
    
    public void setBasi() {
        Mondox=p.FinalAP*20;
        Mondoy=p.FinalAP*13;
        velocita=4;
        direzione="giu";
    }
    
    public void getImmagineG() {
        try{
            giu1 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/giu1.png"));
            giu2 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/giu2.png"));
            su1 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/su1.png"));
            su2 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/su2.png"));
            sinistra1 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/sinistra1.png"));
            sinistra2 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/sinistra2.png"));
            destra1 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/destra1.png"));
            destra2 = ImageIO.read(getClass().getResourceAsStream("/immagini/giocatore/destra2.png"));
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void muovi() {
        
        if(t.su==true||t.giu==true||t.sinistra==true||t.destra==true) {
            if (t.su == true) {
                direzione = "su";
                Mondoy -= velocita;
            } else if (t.giu == true) {
                direzione = "giu";
                Mondoy += velocita;
            } else if (t.sinistra == true) {
                direzione = "sinistra";
                Mondox -= velocita;
            } else if (t.destra == true) {
                direzione = "destra";
                Mondox += velocita;
            }

            Contatore++;
            if (Contatore > 12) {
                if (Num == 1) {
                    Num = 2;
                } else if (Num == 2) {
                    Num = 1;
                }
                Contatore = 0;
            }
        }
    }
    
    public void draw(Graphics2D g2) {
        BufferedImage image=null;
        
        switch(direzione) {
            case "su":
                if(Num==1) {
                    image = su1;
                }
                if(Num==2) {
                    image=su2;
                }
                break;
            case "giu":
                if (Num == 1) {
                    image = giu1;
                }
                if (Num == 2) {
                    image = giu2;
                }
                break;
            case "sinistra":
                if (Num == 1) {
                    image = sinistra1;
                }
                if (Num == 2) {
                    image = sinistra2;
                }
                break;
            case "destra":
                if (Num == 1) {
                    image = destra1;
                }
                if (Num == 2) {
                    image = destra2;
                }
                break;
        }
        g2.drawImage(image,schermoX,schermoY,p.FinalAP,p.FinalAP,null);
    }
}