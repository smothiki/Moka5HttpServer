
import java.io.File;

import java.util.ArrayList;


 
public class List 
{
 
	//File check for a given path 
	public boolean isFile(String arg)
	{
		File file = new File(arg);
		return file.isFile();
	
	}
	
	//Directory check for a given path 
	public boolean isDir(String arg)
	{
		File file = new File(arg);
		return file.isDirectory();
	
	}
	
	//index.html check for a given path 
	public boolean lookforIndex(String path)
	{

		File folder=new File(path);
		if(folder.isDirectory())
		{
			File[] listOfFiles=folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) 
			{
				if(listOfFiles[i].getName().equals("index.html"))
				{
					return true;
				}
			}		
		}
		return false;
	}

	//list the contents of a given path 
	public ArrayList<ArrayList<String> > listNames(String arg) 
	{
	
		ArrayList<ArrayList<String> > file_dir=new ArrayList<ArrayList<String>>();
		ArrayList<String> files=new ArrayList<String>();
		ArrayList<String> dirs=new ArrayList<String>();
		File folder = new File(arg);
		File[] listOfFiles = folder.listFiles(); 
		for (int i = 0; i < listOfFiles.length; i++) 
		{	  
			if (listOfFiles[i].isFile()) 
			{
				files.add(listOfFiles[i].getName());
			}
			if(listOfFiles[i].isDirectory())
			{
				dirs.add(listOfFiles[i].getName());
			}
		}
		file_dir.add(dirs);
		file_dir.add(files);
		return file_dir;
	}

}