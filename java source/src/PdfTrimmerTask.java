import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import de.mz.jk.jsix.libs.XFiles;

public class PdfTrimmerTask extends Thread 
{
	public static float leftMargin = 3.3f;
	public static float rightMargin = 4f;
	public static float topMargin = 7.5f;
	public static float bottomMargin = 57.5f;
	
	public static String resultFileSuffix = "_trimmed";
	private File inFile = null;
	private File outDir = null;
	private File outFile = null;
	
	public PdfTrimmerTask(File file, File dir) throws Exception 
	{
		inFile = file;
		outDir = dir;
		if( file==null || !file.canRead() )
			throw new Exception("invalid input file: " + file.getAbsolutePath());
		
		if(dir == null || !dir.canWrite() )
			throw new Exception("invalid output directory: " + dir.getAbsolutePath());
		
		String baseName = XFiles.getBaseName(inFile);
		outFile = new File( outDir, baseName + resultFileSuffix + ".pdf" );
		
		System.out.println("working directory: " + dir.getAbsolutePath());
		System.out.println("       input file: " + inFile.getName());
		System.out.println("      output file: " + outFile.getName());
		System.out.println("          margins: left=" + leftMargin + "%, right=" + rightMargin +"%, top=" + topMargin + "%, bottom="+ bottomMargin + "%" );
	}
	
	@Override
	public void run() 
	{
		System.out.println("	... processing input file ");
		try
		{
			PdfReader pdfReader = new PdfReader(inFile.getAbsolutePath());
			PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(outFile));
	
			System.out.println("	... input file has " + pdfReader.getNumberOfPages() + " pages");
			
			for (int i = 1; i <= pdfReader.getNumberOfPages(); i++)
		    {
				System.out.println("		... processing page " + i);
				Rectangle cropBox = pdfReader.getCropBox(i);		        
		        float width = cropBox.getRight() - cropBox.getLeft();
		        float height = cropBox.getTop() - cropBox.getBottom();
		        System.out.println("		... page size: width=" + width + ", height=" + height );       
		        PdfArray newCropBox = new PdfArray(
		        		new float[] {
		        				cropBox.getLeft() + width*leftMargin/100f, 
		        				cropBox.getBottom() + height*bottomMargin/100f,
		        				cropBox.getRight() - width*rightMargin/100f, 
		        				cropBox.getTop() - height*topMargin/100f 
	    				}
		        );	
		        PdfDictionary pageDictionary = pdfReader.getPageN(i);
		        pageDictionary.put(PdfName.CROPBOX, newCropBox);
		        pageDictionary.put(PdfName.MEDIABOX, newCropBox);
		    }
			pdfStamper.close();
			pdfReader.close();
			System.out.println("	... processing input file finished!");
		} 
		catch(Exception e)
		{
			System.out.println("ERROR: could not process input file!");
			e.printStackTrace();
		}
	}
}
