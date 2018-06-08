package io.github.lhilbert.imagej;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.scijava.ItemIO;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.ImagePlus;
import loci.formats.FormatException;
import loci.formats.in.OBFReader;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.UnsignedShortType;

@Plugin( type = Command.class, headless = true, menuPath = "Plugins>Bio-Formats>Read .msr files" )
public class ExtractHyperstack implements Command {
	
	private static final Object[] Img = null;

	@Parameter
	private ImageJ refIJ;
	
	// The following service parameters are populated automatically
	// by the service framework before the command is executed.
	//
	// The LogService is used for writing messages to a log, while
	// the StatusService is used to control the ImageJ status bar.

	@Parameter
	private LogService log;

	@Parameter
	private StatusService statusService;

	@Parameter(label = "Extract channels (separate by comma, no spaces)")
	private String channelsString;

	@Parameter
	private File file;
		
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
						
// BF.openImagePlus, only gets one Image out of several...	
		ImagePlus[] readImages = null;
		try {
			
			ImporterOptions options = new ImporterOptions();
			options.setOpenAllSeries(true);
			options.setId(file.getAbsolutePath());
//			options.setSeriesOn(3, true);
			readImages = BF.openImagePlus(options);
			
		} catch (FormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int numChannels = readImages.length;
		for (int ii = 0; ii < numChannels; ii++) {
			ImagePlus thisImg = readImages[ii];
			refIJ.ui().show(thisImg);
			outputImg = refIJ.convert().convert(thisImg,Img.class);
		}
		
		System.out.println(readImages.length);
		System.out.println(readImages[4]);
				
//		for (ImagePlus imagePlus : readImages) {
////			outImageList.add(refIJ.convert().convert(imagePlus, Img.class));
//			System.out.println(imagePlus);
//		}
//		
//		outputImg = outImageList.get(outImageList.size());
	
//		ClassList<IFormatReader> cl = new ClassList<>(IFormatReader.class);
//		cl.addClass(OBFReader.class);
//		ImageReader reader = new ImageReader(cl);
//
		
//		OBFReader reader = new OBFReader();
//		try {
//			reader.setId(file.getAbsolutePath());
//		} catch (FormatException | IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		try {
//
//			for(int i = 0; i < reader.getSeriesCount(); i++) {
//				reader.setSeries(i);
////				Object thisPlane = reader.openPlane(0, 0, 0, reader.getSizeX(), reader.getSizeY());
//				Object data = reader.openPlane(0, 0, 0, reader.getSizeX(), reader.getSizeY());
//				
//				
////				ImageProcessorReader p_reader = new ImageProcessorReader();
////				p_reader.setId(file.getAbsolutePath());
////				
////				ImageProcessor this_processor = p_reader.openProcessors(0, 0, 0, reader.getSizeX(), reader.getSizeY())[0];
////				ImageStack stack = new ImageStack(reader.getSizeX(), reader.getSizeY());
////				stack.addSlice("" + (i + 1), p_reader);
//				
//				byte[] bytes = (byte[])data;
//				short[] shorts = new short[bytes.length/2];
//				ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
//				ArrayImg<UnsignedShortType, ShortArray> img = ArrayImgs.unsignedShorts(shorts, reader.getSizeX(), reader.getSizeY());
//				outputImg = img;
//				refIJ.ui().show(img);
//			}
////			System.out.println(extractedImagesList);
//		} catch (FormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}

	public void tester() {
		
	}
	
	public static void main( final String... args ) throws Exception {
		
		// Launch ImageJ as usual.
		final ImageJ ij = new ImageJ();
		ij.launch(args);

		// invoke the plugin
		ij.command().run( ExtractHyperstack.class, true);
		
	}

}