package com.skedgo.tripgo.tools.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.skedgo.tools.InputCreatorListener;
import com.skedgo.tools.model.StringsStructure;
import com.skedgo.tools.platform.ios.IOSInputStrategy;
import com.skedgo.tools.platform.java.JavaPropertiesOutputStrategy;

public class StringsGeneratorUtils {

	private static StringsGeneratorUtils instance;

	public static final String DEFAULT_LANG = "en";

	private StringsGeneratorUtils() {
	}

	public static StringsGeneratorUtils getInstance() {
		if (instance == null) {
			instance = new StringsGeneratorUtils();
		}
		return instance;
	}

	public void transformAllStrings(String destStringPath, String translationsPath,
			 List<String> iosStringsList, List<String> langs) {

		for (int i = 0; i < iosStringsList.size(); i++) {
			transformAllStrings(iosStringsList.get(i), destStringPath, translationsPath,
					iosStringsList.get(i), langs);
		}
		
	}

	public void transformAllStrings(final String fileName,final  String destJavaStringPath, final String translationsPath,
			String iOSStringFileName, List<String> langs) {

		try (DirectoryStream<Path> directoryStream = Files
				.newDirectoryStream(FileSystems.getDefault().getPath(translationsPath), new DirectoriesFilter())) {

			for (Path path : directoryStream) {

				final String lang = path.getFileName().toString();

				if (skipLang(lang, langs)) {
					continue;
				}

				InputStream input = readFile(translationsPath + "/" + lang + "/" + iOSStringFileName);

				IOSInputStrategy inputStrategy = IOSInputStrategy.getInstance();
				final JavaPropertiesOutputStrategy outputStrategy = JavaPropertiesOutputStrategy.getInstance();

				inputStrategy.createInputValues(input, new InputCreatorListener() {
					
					@Override
					public void didFinishInputCreation(StringsStructure structure) {
						structure = outputStrategy.preprocessInputNames(structure);
						String output = outputStrategy.generateOutput(structure);
						
						String[] fileNameSplit = fileName.split("\\.");

						try {
							writeFile(destJavaStringPath + "/", fileNameSplit[0] + "_" + lang + ".properties", output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
				});
				
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private boolean skipLang(String langToCheck, List<String> langs) {

		for (String lang : langs) {
			if (langToCheck.contains(lang)) {
				return false;
			}
		}
		return true;
	}

	private InputStream readFile(String path) throws IOException {
		File file = new File(path);
		return new FileInputStream(file);
	}

	private void writeFile(String dirPath, String fileName, String content) throws IOException {

		Path parentDir = Paths.get(dirPath);
		Path filePath = Paths.get(dirPath + fileName);

		if (!Files.exists(parentDir))
			Files.createDirectories(parentDir);

		if (Files.exists(filePath)) {
			new PrintWriter(dirPath + fileName).close();
		}

		Files.write(filePath, content.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.CREATE);
	}

	public static class DirectoriesFilter implements Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry);
		}
	}

}
