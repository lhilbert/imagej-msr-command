package io.github.lhilbert.imagej;

import loci.formats.ClassList;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.in.OBFReader;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.scijava.Context;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import io.scif.SCIFIO;
import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

@Plugin( type = Command.class, menuPath = "Plugins>Bio-Formats>Read .msr files" )
public class ExtractHyperstack implements Command {
	
	// The following service parameters are populated automatically
	// by the service framework before the command is executed.
	//
	// The LogService is used for writing messages to a log, while
	// the StatusService is used to control the ImageJ status bar.

	@Parameter
	private ImageJ refIJ;
	
	@Parameter
	private UIService uiService;
	
	@Parameter
	private LogService log;

	@Parameter
	private StatusService statusService;

	@Parameter
	private Context context;
	
	@Parameter
	private File inputFile;
		
	@Parameter(type=ItemIO.OUTPUT)
	private Img outputImg;
	
	@Override
	public void run() {
		// This method actually executes the functionality of this Plugin
		
		ImageJ ij = refIJ;		
		
//		Commented out while scifio isn't yet able to open .msr data
//		SCIFIOConfig config = new SCIFIOConfig().imgOpenerSetImgModes(ImgMode.CELL);
//		Object dataset;
//		try {
//			dataset = ij.scifio().datasetIO().open(inputFile.getAbsolutePath());
//			ij.ui().show(dataset);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
				
		ClassList<IFormatReader> cl = new ClassList<>(IFormatReader.class);
		cl.addClass(OBFReader.class);
		ImageReader reader = new ImageReader(cl);

		try {
			reader.setId(inputFile.getAbsolutePath());
		} catch (FormatException | IOException e) {
			throw new RuntimeException(e);
		}

		try {
			List<ArrayImg<UnsignedShortType, ShortArray>> extractedImagesList = null;
			for(int i = 0; i < reader.getSeriesCount(); i++) {
				reader.setSeries(i);
				Object data = reader.openPlane(0, 0, 0, reader.getSizeX(), reader.getSizeY());
				byte[] bytes = (byte[])data;
				short[] shorts = new short[bytes.length/2];
				ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
				ArrayImg<UnsignedShortType, ShortArray> img = ArrayImgs.unsignedShorts(shorts, reader.getSizeX(), reader.getSizeY());
				extractedImagesList.add(img);
				outputImg = img;
			}
			System.out.println(extractedImagesList);
		} catch (FormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main( final String... args ) throws Exception {
		
		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();
		
		// invoke the plugin
		ij.command().run( ExtractHyperstack.class, true);

		ij.ui().showUI();
		
	}

}