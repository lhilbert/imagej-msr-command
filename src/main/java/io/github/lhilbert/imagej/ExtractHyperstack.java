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
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Plugin( type = Command.class, menuPath = "Plugins>Bio-Formats>Read .msr files" )
public class ExtractHyperstack implements Command {

	@Parameter
	private File inputFile;
	
	@Override
	public void run() {
	}

	public static void main( final String... args ) throws Exception {
		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();

		// ask the user for a file to open
		final File file = ij.ui().chooseFile( null, "open" );
		// use a specific file
//		final File file = new File("/path/to/image.msr");

		ClassList<IFormatReader> cl = new ClassList<>(IFormatReader.class);
		cl.addClass(OBFReader.class);
		ImageReader reader = new ImageReader(cl);

		try {
			reader.setId(file.getAbsolutePath());
		} catch (FormatException | IOException e) {
			throw new RuntimeException(e);
		}

		try {
			for(int i = 0; i < reader.getSeriesCount(); i++) {
				reader.setSeries(i);
				Object data = reader.openPlane(0, 0, 0, reader.getSizeX(), reader.getSizeY());
				byte[] bytes = (byte[])data;
				short[] shorts = new short[bytes.length/2];
				ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
				ArrayImg<UnsignedShortType, ShortArray> img = ArrayImgs.unsignedShorts(shorts, reader.getSizeX(), reader.getSizeY());
				ij.ui().show(img);
			}
		} catch (FormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// invoke the plugin
		ij.command().run( ExtractHyperstack.class, true);

	}

}