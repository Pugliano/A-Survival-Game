/**
 * @author Pugliano Denis, Occhiato Andrea
 * @version 1.0
 * @file Pannello.java
 * @brief Classe Per gestire tutto
 */

package a.survival.game;

import Entita.Entita;
import Entita.Giocatore;
import Entita.Mob;
import Entita.NPC;
import blocchi.Gestione;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JPanel;

/**
 * @class Pannello
 * @brief extends JPanel e implements Runnable
 */
public class Pannello extends JPanel implements Runnable {

    /**altezza del personaggio e degli npc*/
    final int altezzaP = 40; 
    /**altezza in scala 2*/
    final int scala = 2;

    /**altezza final del personaggio*/
    public final int FinalAP = altezzaP * scala; //64*64
    /**altezza schermo x*/
    public final int AltezzaSMX = 24;
    /**
     * larghezza schermo y
     */
    public final int AltezzaSMY = 15;
    /**largezza schermo*/
    public final int FinestraL = FinalAP * AltezzaSMX; //768px->altezza
    /**
     * sltezza schermo
     */
    public final int FinestraA = FinalAP * AltezzaSMY; //576px->larghezza

    /**impostazioni mappa*/
    public final int WordCol = 150;
    public final int WordRig = 150;
    /**schermo intero*/
    int IntFinestraL = FinestraL;
    int IntFinestraA = FinestraA;
    BufferedImage schermoTemp;
    Graphics2D g2;

    /**fps*/
    int fps = 60;

    /**gioco*/
    Gestione GB = new Gestione(this);
    public Tastiera tastiera = new Tastiera(this);
    Mouse mouse = new Mouse(this);

    /**Suoni*/
    Suoni musica = new Suoni();
    Suoni effs = new Suoni();

    /**messaggi*/
    public Interfaccia messaggi = new Interfaccia(this);
    public Azioni azioni = new Azioni(this);

    Thread gameThread;

    /**entità e oggetti*/
    public Giocatore player = new Giocatore(this, tastiera);
    public ArrayList<Entita> gestEnt = new ArrayList<>();
    public ArrayList<Entita> gestNPC = new ArrayList<>();
    public ArrayList<Entita> gestMob = new ArrayList<>();
    public ArrayList<Entita> gestOgg = new ArrayList<>();

    /**stato del gioco*/
    public boolean pausa = false;
    public int state;
    public final int menu = 0;
    public final int gioco = 1;
    public final int dialoghi = 2;
    public final int gameover = 3;
    public final int dati = 4;
    
    /**collisioni*/
    public Collisioni collis = new Collisioni(this);
    public Tuttecose sett = new Tuttecose(this);

    /**orario*/
    public Orario ora;

    public Pannello() {
        this.setPreferredSize(new Dimension(FinestraL, FinestraL));
        this.setBackground(new Color(87, 165, 224, 206));
        this.setDoubleBuffered(true);
        this.addKeyListener(tastiera);
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.setFocusable(true);
    }

    public void setGioco() {
        sett.setoggetto();
        sett.setNpc();
        viaMusica(0);
        state = menu;
        
        //schermo temporaneo
        schermoTemp = new BufferedImage(FinestraL, FinestraA, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) schermoTemp.getGraphics();
        schermoIntero();
    }

    public void startGameThread() {
        ora = new Orario(this, 23, 0);
        ora.start();
        gameThread = new Thread(this);
        gameThread.start();
        for (int i = 0; i < gestNPC.size(); i++) {
            gestNPC.get(i).start();
        }
        sett.setMob();
        for (int i = 0; i < gestMob.size(); i++) {
            gestMob.get(i).start();
        }
    }

    /**\brief
     * visualizzo fps
     */
    @Override
    public void run() {

        double intervallo = 1000000000 / fps;
        double delta = 0;
        long rimanente = System.nanoTime();
        long momento;
        long timer = 0;
        int conta = 0;

        while (gameThread != null) {

            momento = System.nanoTime();

            delta += (momento - rimanente) / intervallo;
            timer += momento - rimanente;
            rimanente = momento;

            if (delta >= 1) {
                muovi();
                drawSchermoTemp();
                drawSchermo();
                delta--;
                conta++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + conta);
                conta = 0;
                timer = 0;
            }
        }
    }

    public void muovi() {
        if (!pausa) {
            //giocatore
            player.muovi();
        }
    }

    /**\brief
     * metto a schermo intero
     */
    public void drawSchermoTemp() {
        //Menu
        if (state == menu) {
            messaggi.draw(g2);
        } //altro
        else {
            //blocchi
            GB.draw(g2);

            //aggiungo umani alla lista
            gestEnt.add(player);

            //npc
            for (int i = 0; i < gestNPC.size() ;i++) {
                if (gestNPC.get(i) != null) {
                    gestEnt.add(gestNPC.get(i));
                }
            }

            //oggetti
            for (int i = 0; i < gestOgg.size(); i++) {
                if (gestOgg.get(i) != null) {
                    gestEnt.add(gestOgg.get(i));
                }
            }

            //nemici
            for (int i = 0; i < gestMob.size(); i++) {
                if (gestMob.get(i) != null) {
                    gestEnt.add(gestMob.get(i));
                }
            }

            //sort
            Collections.sort(gestEnt, new Comparator<Entita>() {
                @Override
                public int compare(Entita e1, Entita e2) {
                    int result = Integer.compare(e1.x, e2.y);
                    return result;
                }
            });

            //umani
            for (int i = 0; i < gestEnt.size(); i++) {
                gestEnt.get(i).draw(g2);
            }

            //orario
            orarioMappa();

            //resetto
            gestEnt.clear();

            //messaggi
            messaggi.draw(g2);
        }
    }

    public void drawSchermo() {
        Graphics g = getGraphics();
        g.drawImage(schermoTemp, 0, 0, IntFinestraL, IntFinestraA, null);
    }

    public void schermoIntero() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(ASurvivalGame.finestra);
        IntFinestraL = ASurvivalGame.finestra.getWidth();
        IntFinestraA = ASurvivalGame.finestra.getHeight();
    }

    /**\brief
     * via musica
     * @param i 
     */
    public void viaMusica(int i) {
        musica.setFile(i);
        musica.vai();
        musica.loop();
    }

    public void stopMusica() {
        musica.stop();
    }

    public void viaEFF(int i) {
        effs.setFile(i);
        effs.vai();
    }

    public void setPausa(boolean p) {
        pausa = p;
    }

    public boolean getPausa() {
        return pausa;
    }

    /**\brift
     * Visualizza orario nel gioco
     */
    public void orarioMappa() {
        g2.setColor(new Color(20, 44, 140));
        if (ora.getOra() < 5) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

        }
        else if (ora.getOra() < 8) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        }
        else if (ora.getOra() < 20) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
        }
        else if (ora.getOra() < 24) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        }
        g2.fillRect(0, 0, FinestraL, FinestraA);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
