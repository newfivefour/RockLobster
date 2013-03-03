import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import utils.FileUtils;
import entities.BlogPost;
import entities.FileTemplate;
import entities.FileTemplateFactory;


public class BlogFileCreationUtils {
	
	public static void createPosts(List<BlogPost> bps, List<FileTemplateFactory> factories) throws FileNotFoundException {
		// Get the file templates from the factories
		for (FileTemplateFactory factory : factories) {
			List<FileTemplate> templates = factory.generatePages(bps);
			// Output them to the fs
			for (FileTemplate fileTemplate : templates) {
				String filename = fileTemplate.getPostProcessedFilename();
				FileUtils.createDir(Main.sOutputDir);
				PrintWriter pw = new PrintWriter(Main.sOutputDir + filename);
				pw.print(fileTemplate.getContent());
				pw.close();
			}
		}	
	}

}
