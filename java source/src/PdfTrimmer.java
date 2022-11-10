/** ezRunner, ezRunner, 04.09.2018*/


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI.FileActionListener;
import de.mz.jk.jsix.utilities.Settings;

/**
 * <h3>{@link PdfTrimmer}</h3>
 * @author Dr. Joerg Kuharev
 * @version 04.09.2018 07:38:21
 */
public class PdfTrimmer implements FileActionListener
{

	public static String version = "20221109";
	
	private String className = this.getClass().getName();
	private Settings cfg = new Settings(className+".ini", className + " configuration file");
	
	private TextWindowDragAndDropUI win = null;

	private static String endl = "\n";
	private static String welcomeMessage = ""
			+ "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = " + endl
			+ "PdfTrimmer crops contents of a PDF file by user defined margins." + endl + endl
			+ "usage: " + endl
			+ "- adjust parameters in the config file" + endl
			+ "- drag and drop a PDF file over this window" + endl
			+ "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = " + endl
			+ "'THE BEER-WARE LICENSE':" + endl
			+ "As long as you retain this notice you can do" + endl
			+ "whatever you want with this application." + endl
			+ "If we meet some day & you think that the application" + endl
			+ "is worth it, you can buy me a beer in return." + endl
			+ "(c) Joerg Kuharev, 2022" + endl
			+ "= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = " + endl;
	
	public static void main(String[] args) 
	{
		new PdfTrimmer();
	}
	
	public PdfTrimmer() 
	{
		win = new TextWindowDragAndDropUI("PdfTrimmer (build:"+version+")", 640, 480, welcomeMessage);
		win.addFileActionListener(this);
		initConfig();
	}
	
	private void initConfig() 
	{
		PdfTrimmerTask.leftMargin = cfg.getFloatValue("leftMarginPercent", PdfTrimmerTask.leftMargin, false);
		PdfTrimmerTask.rightMargin = cfg.getFloatValue("rightMarginPercent", PdfTrimmerTask.rightMargin, false);
		PdfTrimmerTask.topMargin = cfg.getFloatValue("topMarginPercent", PdfTrimmerTask.topMargin, false);
		PdfTrimmerTask.bottomMargin = cfg.getFloatValue("bottomMarginPercent", PdfTrimmerTask.bottomMargin, false);
		PdfTrimmerTask.resultFileSuffix = XJava.stripQuotation( cfg.getStringValue("resultFileSuffix", PdfTrimmerTask.resultFileSuffix, false) );
	}
	
	@Override
	public List<File> filterTargetFiles(List<File> files) 
	{
		List<File> res = new ArrayList<File>();
		for(File f : files)
		{			
			try 
			{
				if( f.exists() && f.canRead() && f.getName().toLowerCase().endsWith(".pdf") )
				{
					res.add(f);
				}
				else
				{
					System.out.println("file is NOT a valid, readable PDF: " + f.getAbsolutePath());
				}
			}
			catch (Exception e) 
			{
				System.err.println("something wrong with the file: " + f.getAbsolutePath());
			}
		}
		return res;
	}

	@Override
	public void doMultiFileAction(List<File> files) 
	{
		
	}

	@Override
	public void doSingleFileAction(File file) 
	{
		try
		{
			initConfig();
			PdfTrimmerTask task = new PdfTrimmerTask(file, file.getParentFile());
			task.start();
			task.join();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}