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

public class Captura {
    public static void main(String arg[]) throws FrameGrabber.Exception, InterruptedException, IOException {
        KeyEvent tecla = null;
        // inicia as bibliotecas openCV
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        //inicia o reconhecimnto da câmera no dispositivo atual
        camera.start();
        // inicia o parâmetro ultimo_ID para verificar quantos IDs já foram registrados
        int ultimo_ID=0;
        // abre o arquivos de IDs armazenados
        FileReader arq = new FileReader("src\\recursos\\ids.txt");
        BufferedReader lerArq = new BufferedReader(arq);
        String linha = lerArq.readLine();
        // faz a leitura das linhas no arquivo de IDs
        while (linha != null) {
            // ignora a primeira linha que corresponde ao cabeçalho
            if (linha.split("\\;")[0].equalsIgnoreCase("id")){linha = lerArq.readLine();continue;}
            System.out.printf("Id existente: %s\n", linha.split("\\;")[0]);
            ultimo_ID = Integer.parseInt(linha.split("\\;")[0]);
            linha = lerArq.readLine(); 
        }
        // fecha o arquivo de IDs aberto somente para leitura de IDs existentes e o parâmetro ultimo_ID é preenchido
        arq.close();
        // prepara o arquivos de IDs para escrita
        FileWriter ids = new FileWriter("src\\recursos\\ids.txt",true);
        PrintWriter gravarArq = new PrintWriter(ids);
        // inicia a biblioteca de classificador de imagens utilizando arquivo haarcascade_frontalface_alt.xml
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");        
        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;
        Mat imagemColorida = new Mat();
        // prepara os parâmetros para iniciar a coleta de 25 imagens
        int numeroAmostras = 25;
        int amostra = 1;
        // solicita o nome do usuário
        System.out.println("Digite seu Nome: ");
        Scanner nome = new Scanner(System.in);
        String nomePessoa = nome.next();
        // incrementa o ultimo ID existente com mais 1 número
        int idPessoa = ultimo_ID+1;
        // soliita o nível do usuário
        System.out.println("Digite seu nível: ");
        Scanner nivel = new Scanner(System.in);
        int idNivel = nivel.nextInt();
        // enquanto a câmera estiver aberta e detectando uma face a estrutura abaixo se repete por 25 vezes enquanto o usuário interage via tecla 'q'
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            //os frames capturados pela câmera são são convertidos para o padrão informado no haarcascade_frontalface_alt.xml e depois comparadas             
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150,150), new Size(500,500));
            if (tecla == null) {
                tecla = cFrame.waitKey(5);
            }
            // para cada face detectada é feita uma captura para o formato jpg          
            for (int i=0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(0);
                // abre um quadro na imagem detectada pela câmera                
                rectangle(imagemColorida, dadosFace, new Scalar(0,0,255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160,160));
                if (tecla == null) {
                    tecla = cFrame.waitKey(5);
                }
                // a medida que o usuário pressiona a tecla 'q' uma imagem é armazenada no formato <nome>.<id>.<numero-amostra>.<nível> na pasta fotos
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
            // após chegar no número de 25 fotos armazemanadas, câmera é fechada e a sai da estrutura de repetição
            if (amostra > numeroAmostras) {
                break;
            }
        }
        // o arquivo de IDs é atualizado com as informações de nome, id e nível
        gravarArq.printf("\n"+idPessoa+";"+nomePessoa+";"+idNivel);
        ids.close();
        cFrame.dispose();
        camera.stop();
    }
}
