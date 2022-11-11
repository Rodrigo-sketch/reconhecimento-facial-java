/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reconhecimento;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.IntBuffer;
import java.util.ArrayList;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;


/**
 *
 * @author Rodrigo
 */
public class Treinamento1 {
     public static void main(String args[]) {
        File diretorio = new File("src\\fotos");
        FilenameFilter filtroImagem = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String nome) {
                return nome.endsWith(".jpg") || nome.endsWith(".gif") || nome.endsWith(".png");
            }
        };
        
        File[] arquivos = diretorio.listFiles(filtroImagem);
        MatVector fotos = new MatVector(arquivos.length);
        Mat rotulos = new Mat(arquivos.length, 1, CV_32SC1);
        IntBuffer rotulosBuffer = rotulos.createBuffer();
        int contador = 0;
        String labelsInfo = "";        
        ArrayList<String> nome = new ArrayList<>();
        ArrayList<Integer> id = new ArrayList<>();
        ArrayList<String> nivel = new ArrayList<>();
        String strnivel = "";
        String vars[] = null;
         for (int i=0;i<arquivos.length;i++) {
            Mat foto = imread(arquivos[i].getAbsolutePath(), IMREAD_GRAYSCALE);
            int classe = Integer.parseInt(arquivos[i].getName().split("\\.")[1]);
            resize(foto, foto, new Size(160,160));
            fotos.put(contador, foto);
            rotulosBuffer.put(contador, classe);
            contador++;
            if (!labelsInfo.equalsIgnoreCase(arquivos[i].getName().split("\\.")[0])){
                vars = arquivos[i].getName().split("\\.");
                nome.add(vars[0]);
                id.add(Integer.parseInt(vars[1]));
                strnivel = vars[3];
                strnivel = strnivel.replace("(", "");
                strnivel = strnivel.replace(")", "");
                nivel.add(strnivel);
            }
            labelsInfo = arquivos[i].getName().split("\\.")[0]; 
        }
        
        EigenFaceRecognizer eigenfaces = EigenFaceRecognizer.create().capacity(30);
        ArrayList<Integer> pos = new ArrayList<>();
        for(int j=0;j<id.size();j++){
            for (int p=0;p<3;p++){
                pos.add((id.get(j)*10)+p);
            }
        }
        eigenfaces.train(fotos, rotulos);
                    
        for(int r=0;r<nome.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3), nome.get(r));
        }
        for(int r=0;r<id.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3)+1, Integer.toString(id.get(r)));
        }
        for(int r=0;r<nivel.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3)+2, nivel.get(r));
        }
        eigenfaces.save("src\\recursos\\classificadorEigenFaces.yml");
    }
}
