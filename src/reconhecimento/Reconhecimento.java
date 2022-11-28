/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reconhecimento;

/**
 *
 * @author Rodrigo
 */

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import static org.bytedeco.opencv.global.opencv_imgproc.FONT_HERSHEY_PLAIN;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*; 
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.putText;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Reconhecimento {
    public static void main(String args[]) throws FrameGrabber.Exception, InterruptedException {
        // inicia as bibliotecas openCV
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        //inicia o reconhecimnto da câmera no dispositivo atual
        camera.start();
        // inicia a biblioteca de classificador de imagens utilizando arquivo haarcascade_frontalface_alt.xml
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
        // iniciar a biblioteca EingenFaces para abrir o arquivo classificadorEigenFaces.yml que contém as informações das fotos armazenadas
        FaceRecognizer reconhecedor = EigenFaceRecognizer.create().capacity(30);
        reconhecedor.read("src\\recursos\\classificadorEigenFaces.yml");
        
        // inicia um arrayList para buscar informações no arquivo classificadorEigenFaces.yml. As informações estão armazenadas por ID em cada label. 
        // para cada grupo de pessoas os Ids de label estão separados por ordem a cada 10 IDs, ex: usuário1 - IDs. 10,11,12  usuário2 - IDs 20,21,22
        ArrayList<String> pessoas = new ArrayList<>();
        int p = 10;
        // a estrutura abaixo busca o ID correspondente ao nome das pessoas armazenadas no arquivo classificadorEigenFaces.yml em labelsInfo
        while (reconhecedor.getLabelInfo(p).get()>0) {
            pessoas.add(reconhecedor.getLabelInfo(p).getString()+" -- "+reconhecedor.getLabelInfo(p+2).getString());
            p = p + 10;
        }
        
        String nome = ""; 
        boolean falhou = false;
        long start = System.currentTimeMillis();
        long current = 11;
        long fim = 0;
        KeyEvent tecla = null;
        //abre a câmera para iniciar o reconhecimento
        CanvasFrame cFrame = new CanvasFrame("Reconhecimento", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
        // enquanto a câmera estiver aberta e detectando uma face a estrutura abaixo se repete por 10 segundos até o reconhecimento
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            //os frames capturados pela câmera são são convertidos para o padrão informado no haarcascade_frontalface_alt.xml e depois comparadas 
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 2, 0, new Size(100,100), new Size(500,500));
            if (tecla == null) {
                tecla = cFrame.waitKey(2);
            }
            // inicia o contador de tempo de 10 segundos
            fim = System.currentTimeMillis()-start;
            
            
            if (facesDetectadas.size()==0 && fim >= 10000){
                falhou = true;
            }
            
            if (facesDetectadas.size()>=1 && fim >= 10000){
                falhou = false;
            }
            // para cada face detectada é feita uma comparação com os dados contidos no arquivo classificadorEigenFaces.yml
            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(i);
                // abre um quadro na imagem detectada pela câmera
                rectangle(imagemColorida, dadosFace, new Scalar(0,255,0,0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160,160));
                
                IntPointer rotulo = new IntPointer(1);
                DoublePointer confianca = new DoublePointer(1);
                // verifica o nível de confiança da face detectada
                reconhecedor.predict(faceCapturada, rotulo, confianca);
                int predicao = rotulo.get(0);
                // Caso a imagem que aparece na câmera possui correspondente é informado.
                if (predicao == -1) {
                    nome = "Desconhecido";
                } else {
                    nome = pessoas.get(predicao-1);
                }
                
                int x = Math.max(dadosFace.tl().x() - 10, 5);
                int y = Math.max(dadosFace.tl().y() - 10, 5);
                // o texto com o nome da pessoa aparece no quadro desenhado na imagem detectada, caso exista
                putText(imagemColorida, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.7, new Scalar(0,255,0,0));
            }
            if (cFrame.isVisible()) {
                cFrame.showImage(frameCapturado);
            }
            // Ao fim dos 10 segundo ou após clicar a tecla 'q' o processo de detecção é interrompido e a mensagem final é exibida
            if ((tecla != null && tecla.getKeyChar() == 'q')||(fim > 20000) || (!falhou && fim > 10000)) {
                System.out.println("Resultado "+nome);
                if (!nome.equalsIgnoreCase("")&&(!falhou)){
                    System.out.println("Reconhecimento facial realizado! Acesso Liberado!");
                }else{
                    System.out.println("Reconhecimento facial falhou! Acesso Negado!");
                }
                break;
            }
            // imprime o tempo restante para detecção da imagem na cânera
            if (current!=(10 - (fim / 1000) % 60)){
                System.out.println("Segundos restantes:"+ (10 - (fim / 1000) % 60));
                current = (10 - (fim / 1000) % 60);
            }
            // se a contagem regressiva chegar a zero, o sistema informa que o reconhecimento facial falhou!
            if (current == 0){
                System.out.println("Reconhecimento facial falhou! Acesso Negado!");
                break;
            }
        }
        cFrame.dispose();
        camera.stop();
    }
}
