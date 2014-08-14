/*
 *Convolución de una imagen 
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package convolucion;

/**
 *PDS
 * Convolución aplicada al  procesamiento y filtro de imágenes 
 * @author Ricardo.huarte- Luis Vazquez 
 */
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
 
public class Convolucion
{
    //se crea el objeto matriz arraydata se crean sus gets y sets y sus constructores 
  public static class ArrayData
  {
    public final int[] dataArray;
    public final int width;
    public final int height;
 
    public ArrayData(int width, int height)
    {
      this(new int[width * height], width, height);
    }
 
    public ArrayData(int[] dataArray, int width, int height)
    {
      this.dataArray = dataArray;
      this.width = width;
      this.height = height;
    }
 
    public int get(int x, int y)
    {  return dataArray[y * width + x];  }
 
    public void set(int x, int y, int value)
    {  dataArray[y * width + x] = value;  }
  }
 
  private static int bound(int value, int endIndex)
  {
    if (value < 0)
      return 0;
    if (value < endIndex)
      return value;
    return endIndex - 1;
  }
 //este método es el que realiza la convolución, tiene como parametros la matriz
  //de la imagen de entrada, el kernel, entero 
  public static ArrayData convolute(ArrayData inputData, ArrayData kernel, int kernelDivisor)
  {
    int inputWidth = inputData.width;
    int inputHeight = inputData.height;
    int kernelWidth = kernel.width;
    int kernelHeight = kernel.height;
    if ((kernelWidth <= 0) || ((kernelWidth & 1) != 1))
      throw new IllegalArgumentException("Kernel must have odd width");//se verifica que la matriz sea de numeros impares
    if ((kernelHeight <= 0) || ((kernelHeight & 1) != 1))
      throw new IllegalArgumentException("Kernel must have odd height");
    int kernelWidthRadius = kernelWidth >>> 1;
    int kernelHeightRadius = kernelHeight >>> 1;
 
    ArrayData outputData = new ArrayData(inputWidth, inputHeight);
    for (int i = inputWidth - 1; i >= 0; i--)
    {
      for (int j = inputHeight - 1; j >= 0; j--)
      {
        double newValue = 0.0;
        for (int kw = kernelWidth - 1; kw >= 0; kw--)
          for (int kh = kernelHeight - 1; kh >= 0; kh--)
            newValue += kernel.get(kw, kh) * inputData.get(
                          bound(i + kw - kernelWidthRadius, inputWidth),
                          bound(j + kh - kernelHeightRadius, inputHeight));
        outputData.set(i, j, (int)Math.round(newValue / kernelDivisor));
      }
    }
    return outputData;
  }
 //Obtiene y transforma la imagen de entrada 
  public static ArrayData[] getArrayDatasFromImage(String filename) throws IOException
  {
    BufferedImage inputImage = ImageIO.read(new File(filename));
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    int[] rgbData = inputImage.getRGB(0, 0, width, height, null, 0, width);
    ArrayData reds = new ArrayData(width, height);
    ArrayData greens = new ArrayData(width, height);
    ArrayData blues = new ArrayData(width, height);
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        int rgbValue = rgbData[y * width + x];
        reds.set(x, y, (rgbValue >>> 16) & 0xFF);
        greens.set(x, y, (rgbValue >>> 8) & 0xFF);
        blues.set(x, y, rgbValue & 0xFF);
      }
    }
    return new ArrayData[] { reds, greens, blues };
  }
 //Función que guarda la nueva imagen procesaa
  public static void writeOutputImage(String filename, ArrayData[] redGreenBlue) throws IOException
  {
    ArrayData reds = redGreenBlue[0];
    ArrayData greens = redGreenBlue[1];
    ArrayData blues = redGreenBlue[2];
    BufferedImage outputImage = new BufferedImage(reds.width, reds.height,
                                                  BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < reds.height; y++)
    {
      for (int x = 0; x < reds.width; x++)
      {
        int red = bound(reds.get(x, y), 256);
        int green = bound(greens.get(x, y), 256);
        int blue = bound(blues.get(x, y), 256);
        outputImage.setRGB(x, y, (red << 16) | (green << 8) | blue | -0x01000000);
      }
    }
    ImageIO.write(outputImage, "PNG", new File(filename));
    return;
  }
 //se crea el main y se construye el kernel con los valores de usuario 
  public static void main(String[] args) throws IOException
  {
      //---------------------creación del kernel--------------------
    int kernelWidth = Integer.parseInt("3");
    int kernelHeight = Integer.parseInt("3");
    int kernelDivisor = Integer.parseInt("50");
    System.out.println("Kernel size: " + kernelWidth + "x" + kernelHeight +
                       ", divisor=" + kernelDivisor);
    //---------------------creación del kernel--------------------
    
    
    //---------------------Aquí se llena el arreglo o kernel--------------------
    int y = 1;
    ArrayData kernel = new ArrayData(kernelWidth, kernelHeight);
    for (int i = 0; i < kernelHeight; i++)
    {
      System.out.print("[");
      for (int j = 0; j < kernelWidth; j++)
      {
          //para probar diferentes filtros descomente el que va a utilizar y comente los demas 
          
          
        //  //----------------
         // //efecto borroso
        kernel.set(j, i, y++); 
        
        //   //----------------
        //  //kernel de 1's 
          
          
//                  kernel.set(j, i, 1);
          
          
      //     //----------------
       // //alto relieve
//        kernel.set(0,0 , -2);   
//        kernel.set(0,1 , -2);
//        kernel.set(0,2 , 0);
//        kernel.set(1,0 , -2);
//        kernel.set(1, 1, 6);
//        kernel.set(1,2 , 0);
//        kernel.set(2, 0, 0);
//        kernel.set(2,1 , 0);
//        kernel.set(2,2 , 0);
        
      //   //----------------
        
        System.out.print(" " + kernel.get(j, i) + " ");//imprime el kernel que llenamos 
      }
      System.out.println("]");
    }
 //obtiene la matriz o arreglo de la imagen de entrada 
    ArrayData[] dataArrays = getArrayDatasFromImage(System.getProperty("user.home")+"\\two.jpg");
//Un ciclo for para cada uno de los pixeles de la imagen de entrada, se ejecuta el metodo 
    //convolute (que es el que convoluciona) con el arreglo de la imagen de entrada,
    //el kernel definido por el usuario y el entero divisor del kernel 
    for (int i = 0; i < dataArrays.length; i++)
      dataArrays[i] = convolute(dataArrays[i], kernel, kernelDivisor);
    //se escribe o se crea el archivo de la matriz o arreglo que se obtuvo después de convolucionar
    //en un archivo definido por el usuario 
    writeOutputImage(System.getProperty("user.home")+"\\convolucion.png", dataArrays);
    return;
  }
}