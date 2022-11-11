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
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        
        camera.start();
        
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");
        
        FaceRecognizer reconhecedor = EigenFaceRecognizer.create().capacity(30);
        reconhecedor.read("src\\recursos\\classificadorEigenFaces.yml");
        
        ArrayList<String> pessoas = new ArrayList<>();
        int p = 10;
        
        while (reconhecedor.getLabelInfo(p).get()>0) {
            pessoas.add(reconhecedor.getLabelInfo(p).getString()+" -- "+reconhecedor.getLabelInfo(p+2).getString());
            p = p + 10;
        }
        
        String nome = ""; 
        boolean falhou = false;
        long start = System.currentTimeMillis();
        long current = 0;
        long fim = 0;
        KeyEvent tecla = null;
        CanvasFrame cFrame = new CanvasFrame("Reconhecimento", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
        
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 2, 0, new Size(100,100), new Size(500,500));
            if (tecla == null) {
                tecla = cFrame.waitKey(2);
            }
            fim = System.currentTimeMillis()-start;
            
            
            if (facesDetectadas.size()==0 && fim >= 20000){
                falhou = true;
            }
            
            if (facesDetectadas.size()>=1 && fim >= 10000){
                falhou = false;
            }
            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(i);
                rectangle(imagemColorida, dadosFace, new Scalar(0,255,0,0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160,160));
                
                IntPointer rotulo = new IntPointer(1);
                DoublePointer confianca = new DoublePointer(1);
                reconhecedor.predict(faceCapturada, rotulo, confianca);
                int predicao = rotulo.get(0);
                
                if (predicao == -1) {
                    nome = "Desconhecido";
                } else {
                    nome = pessoas.get(predicao-1);
                }
                
                int x = Math.max(dadosFace.tl().x() - 10, 5);
                int y = Math.max(dadosFace.tl().y() - 10, 5);
                putText(imagemColorida, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.7, new Scalar(0,255,0,0));
            }
            if (cFrame.isVisible()) {
                cFrame.showImage(frameCapturado);
            }
            if ((tecla != null && tecla.getKeyChar() == 'q')||(fim > 20000) || (!falhou && fim > 10000)) {
                System.out.println("Resultado "+nome);
                if (!nome.equalsIgnoreCase("")&&(!falhou)){
                    System.out.println("Reconhecimento facial realizado! Acesso Liberado!");
                }else{
                    System.out.println("Reconhecimento facial falhou! Acesso Negado!");
                }
                break;
            }
            if (current!=(10 - (fim / 1000) % 60)){
                System.out.println("Segundos restantes:"+ (10 - (fim / 1000) % 60));
                current = (10 - (fim / 1000) % 60);
            }
        }
        cFrame.dispose();
        camera.stop();
    }
}
