#Reconhecimento facial com Java

O reconhecimento facial é umas das subáreas da Inteligência Artificial que tem como objetivo reconhecer faces de pessoas em imagens  ou vídeos. Um exemplo são os sistemas de segurança que podem utilizar  esses recursos para identificar se uma pessoa está ou não presente em um  ambiente. Neste contexto é importante frisar as diferenças entre as  técnicas de detecção e reconhecimento facial. Enquanto a primeira  somente indica se uma face está presente em uma imagem, a segunda  técnica tem o objetivo de dizer de quem é a face detectada.

Com base nisso, código para o reconhecimento facial tem o intuito de reconhecer faces por imagens e  pela webcam. Utilizamos a linguagem Java e a biblioteca JavaCV, que é  uma das mais utilizadas para processamento digital de  imagens e visão computacional. Os passos vão desde a criação das imagens de  treinamento, a aprendizagem dos algoritmos e finalmente o reconhecimento  de quem é quem! O algoritmo disponibilizado no JavaCV utilizado é o Eigenfaces e pode ser implementá-lo em ambientes comerciais.

O Código foi desenvolvido para que pudesse ser armazenado em arquivo o conjunto de informação como id, nome e nível, sendo que o ID é incremetal para facilitar o reconhecomento. As etapas do reconhecimento saão: 
1 - Detectar faces, por meio da classe Captura. Nessa classe o usuário formece as informações pessoais e são geradas as imagens.
2 - Treinar. nessa etapa o EigenFaces gera o arquivo yml com as imagens coletadas e armazena as informações pessoais.
3 - Reconhecer. nessa etapa a câmera faz o reconhecimento da imagem detectada e compara com as informações guardadas no arquivo yml gerado. Após um intervalo de tempo é informado se a imagem foi reconhecida ou não.

![alt text] (https://github.com/Rodrigo-sketch/reconhecimento-facial-java/blob/main/etapas%20para%20o%20reconhecimento.png)

![alt text] (https://github.com/Rodrigo-sketch/reconhecimento-facial-java/blob/main/eexemplo-reconhecimento-facial.png)

![alt text] (https://github.com/Rodrigo-sketch/reconhecimento-facial-java/blob/main/eigenfaces.png)

![alt text] (https://github.com/Rodrigo-sketch/reconhecimento-facial-java/blob/main/Eigenvetor.png)

![alt text] (https://github.com/Rodrigo-sketch/reconhecimento-facial-java/blob/main/Eigenvetor2.png)
