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
        // inicia a leitura das fotos capturadas e armazenadas na pasta fotos
        File diretorio = new File("src\\fotos");
        FilenameFilter filtroImagem = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String nome) {
                return nome.endsWith(".jpg") || nome.endsWith(".gif") || nome.endsWith(".png");
            }
        };
        
        File[] arquivos = diretorio.listFiles(filtroImagem);
        // verifica o número de fotos existentes
        MatVector fotos = new MatVector(arquivos.length);
        // inicia o rótulo que será gravado
        Mat rotulos = new Mat(arquivos.length, 1, CV_32SC1);
        IntBuffer rotulosBuffer = rotulos.createBuffer();
        // inicia as variáveis de controle e organização do arquivo que sera geradao
        int contador = 0;
        String labelsInfo = "";        
        ArrayList<String> nome = new ArrayList<>();
        ArrayList<Integer> id = new ArrayList<>();
        ArrayList<String> nivel = new ArrayList<>();
        String strnivel = "";
        String vars[] = null;
        // para cada foto armazenada são retiradas informações para geração do arquivo yml
         for (int i=0;i<arquivos.length;i++) {
            Mat foto = imread(arquivos[i].getAbsolutePath(), IMREAD_GRAYSCALE);
            int classe = Integer.parseInt(arquivos[i].getName().split("\\.")[1]);
            resize(foto, foto, new Size(160,160));
            fotos.put(contador, foto);
            rotulosBuffer.put(contador, classe);
            contador++;
            // as informações de nome, id e nível são armazenadas nos Arrays a cada conjunto de 25 fotos analizadas por nome
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
        // iniciar a biblioteca EingenFaces para abrir o arquivo classificadorEigenFaces.yml que contém as informações das fotos armazenadas
        EigenFaceRecognizer eigenfaces = EigenFaceRecognizer.create().capacity(30);
        // É iniciado o aaray que vai guardar a posição de cadas conjunto de informações das fotos capturadas 
        ArrayList<Integer> pos = new ArrayList<>();
        for(int j=0;j<id.size();j++){
            for (int p=0;p<3;p++){
                pos.add((id.get(j)*10)+p);
            }
        }
        // é realizada a escrita do arquivo classificadorEigenFaces.yml com os dados correspondentes a cada uma das fotos
        eigenfaces.train(fotos, rotulos);
        // é realizada a escrita no parâmetro labelsInfo e posicicionado em grupos para cada usuário existente           
        // para cada grupo de pessoas os Ids de label estão separados por ordem a cada 10 IDs, ex: usuário1 - IDs. 10,11,12  usuário2 - IDs 20,21,22
        // abaixo são gravados os nomes 
        for(int r=0;r<nome.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3), nome.get(r));
        }
        // gravação dos IDs para cada usuário em sua posiçao
        for(int r=0;r<id.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3)+1, Integer.toString(id.get(r)));
        }
        // gravação dos níveis para cada usuário em sua posiçao
        for(int r=0;r<nivel.size();r++){
            eigenfaces.setLabelInfo(pos.get(r*3)+2, nivel.get(r));
        }
        // informações são salvas no arquivo classificadorEigenFaces.yml
        eigenfaces.save("src\\recursos\\classificadorEigenFaces.yml");
    }
}
