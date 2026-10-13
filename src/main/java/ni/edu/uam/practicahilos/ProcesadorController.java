package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {
    @FXML private Label lblEstado;
    @FXML private Label lblPorcentaje;
    @FXML private ProgressBar barraProgreso;
    @FXML private Button btnIniciar, btnDetener, btnReiniciar;

    // hilo que ejcuta el procesamiento en segundo plano
    private Thread hiloProceso;

    @FXML
    private void iniciarProceso(){
        // si ya hay un hilo corriendo no arraanca otro.
        if(hiloProceso != null && hiloProceso.isAlive()){
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros... ");

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        //tarea que ejecutara el hilo
        Runnable tarea = () -> {
            try {
                for(int i =1; i<=100;i++){
                    //si piedieron detener, se corta el cilco
                    if(Thread.currentThread().isInterrupted()){
                        throw new InterruptedException();
                    }

                    Thread.sleep(100);

                    //avanace debe ser final para poder usarlo en un lambda
                    final int avance = i;

                    //los controles solo se tocan desde el hilo de javafx
                    Platform.runLater(()->{
                        barraProgreso.setProgress(avance/100.0);
                        lblPorcentaje.setText(avance + "%");
                    });
                }

                Platform.runLater(()->{
                    lblEstado.setText("Proceso finalizado");
                    habilitarBotones();
                });
            } catch (InterruptedException e){
                // se vuelve  a marca rla interrupcion para no perderla.
                Thread.currentThread().interrupt();

                Platform.runLater(()->{
                    lblEstado.setText("Proceso detenido");
                    habilitarBotones();
                });
            }
        };

        hiloProceso = new Thread(tarea);
        hiloProceso.setName("Hilo-Procesamiento");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void reiniciarProceso(){
        //no se puede reiniciar mientras el hilo sigue vivo
        if(hiloProceso != null && hiloProceso.isAlive()){
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        habilitarBotones();
    }

    private void habilitarBotones(){
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    @FXML
    private void detenerProceso(){
        if(hiloProceso != null && hiloProceso.isAlive()){
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detencion..");
            hiloProceso.interrupt();
        }
    }

    // la llama mainapp cuando cierran la ventana para no dejar el hilo colgado

    public void detenerAlCerrar(){
        if(hiloProceso != null && hiloProceso.isAlive()){
            hiloProceso.interrupt();
        }
    }

}

