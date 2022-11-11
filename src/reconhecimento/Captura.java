/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reconhecimento;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

/**
 *
 * @author Rodrigo
 */

public class Captura1 {
    public static void main(String arg[]) throws FrameGrabber.Exception, InterruptedException, IOException {
        KeyEvent tecla = null;
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();
        
        int ultimo_ID=0;
        
        FileReader arq = new FileReader("src\\recursos\\ids.txt");
        BufferedReader lerArq = new BufferedReader(arq);
        String linha = lerArq.readLine();
        while (linha != null) {
            if (linha.split("\\;")[0].equalsIgnoreCase("id")){linha = lerArq.readLine();continue;}
            System.out.printf("Id existente: %s\n", linha.split("\\;")[0]);
            ultimo_ID = Integer.parseInt(linha.split("\\;")[0]);
            linha = lerArq.readLine(); 
        }
        arq.close();
        FileWriter ids = new FileWriter("src\\recursos\\ids.txt",true);
        PrintWriter gravarArq = new PrintWriter(ids);
        
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");        
        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
        int numeroAmostras = 25;
        int amostra = 1;
        System.out.println("Digite seu Nome: ");
        Scanner nome = new Scanner(System.in);
        String nomePessoa = nome.next();
        
        int idPessoa = ultimo_ID+1;
        System.out.println("Digite seu nível: ");
        Scanner nivel = new Scanner(System.in);
        int idNivel = nivel.nextInt();
        
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150,150), new Size(500,500));
            if (tecla == null) {
                tecla = cFrame.waitKey(5);
            }
            for (int i=0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFace, new Scalar(0,0,255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160,160));
                if (tecla == null) {
                    tecla = cFrame.waitKey(5);
                }
                if (tecla != null) {
                    if (tecla.getKeyChar() == 'q') {
                        if (amostra <= numeroAmostras) {
                            imwrite("src\\fotos\\"+nomePessoa+"."+idPessoa + "." + amostra + ".Nivel "+idNivel+".jpg", faceCapturada);
                            System.out.println("src\\fotos\\"+nomePessoa+"."+idPessoa + "." + amostra + ".Nivel "+idNivel+".jpg");
                            System.out.println("Foto " + amostra + " capturada\n");
                            amostra++;
                        }
                    }
                    tecla = null;
                }
            }
            if (tecla == null) {
                tecla = cFrame.waitKey(20);
            }
            if (cFrame.isVisible()) {
                cFrame.showImage(frameCapturado);
            }
            
            if (amostra > numeroAmostras) {
                break;
            }
        }
        gravarArq.printf("\n"+idPessoa+";"+nomePessoa+";"+idNivel);
        ids.close();
        cFrame.dispose();
        camera.stop();
    }
}
