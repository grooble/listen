package com.grooble.android;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.grooble.model.Member;
import com.grooble.model.Person;
import com.grooble.model.Scaler;
import com.grooble.model.Update;

/*
 * Take user profile pic from phone and save to server for reference in 
 * friends list, status and other areas of app
 */

@SuppressWarnings("serial")
public class ImageHandler extends HttpServlet {

    private static final String TAG = "ImageHandler";
    private File tempFile = null;
    
    // Initilize datasource
    private DataSource datasource;
    public void init() throws ServletException {
        try {
            datasource = (DataSource) getServletContext().getAttribute("DBCPool");
        } catch(Exception e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException{
        
        response.setContentType("text/html");
        String email = null;
        String password = null;
        
        Person user = null;
        
        String tempImgPath = null, imgPath = null, thumbPath = null, realPath = null;
        
        // initialize image attributes and filenames
        String fileName = null;
        String extension = null;
        boolean fileFound = false;

        // Initialize Member for later user lookup
        Member member = new Member(datasource);

        // get realPath
        realPath = getServletContext().getRealPath("/");

        //get Factory and parse request FileItems
        try{
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(request);
            
            Iterator<FileItem> it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem) it.next();
                if(item == null){
                    System.out.println(TAG + ": item is null");
                }
                if(item.isFormField()){
                    if(item.getFieldName().equals("email")){
                        email = item.getString();
                    }
                    if(item.getFieldName().equals("password")){
                        password = item.getString();
                    }
                }
                else{ // handle writing of image file
                    fileFound = true;
                    fileName = FilenameUtils.getName(item.getName());
                    extension = FilenameUtils.getExtension(fileName);
                    tempImgPath = FilenameUtils.separatorsToSystem(realPath + "profile" + File.separator );
                    System.out.println(TAG + "...tempImgPath: " + tempImgPath + ", filename: " + fileName);
                    tempFile = new File(imgPath, fileName);
                                        
                    item.write(tempFile);
                }
            }// end while
            
            // lookup and verify user
            if(
                    ((email != null) && (!email.isEmpty())) && 
                    ((password != null) && (!password.isEmpty()))
               ){
                System.out.println(TAG + "...email: " + email + ", pwd: " + password);
                user = member.verify(email.toLowerCase(), password);
                
                // if user is not null, get image and save
                if(user != null){
                    System.out.println(TAG + "...user is not null. userid: " + user.getId());
                    // get paths to save file to
                    imgPath = realPath + "profile" + File.separator + 
                            user.getId() + File.separator + "images" + File.separator;
                    File imgPathDir = new File(imgPath);
                    if (!imgPathDir.isDirectory()){
                        System.out.println(TAG +"...made img dir: " + imgPath);
                        imgPathDir.mkdir();
                    }
                
                    //get thumbnail save path
                    thumbPath = realPath + "profile" + File.separator + 
                            user.getId() + File.separator +"images" +
                            File.separator + "thumb" + File.separator;
                    File thumbPathDir = new File(thumbPath);
                    if(!thumbPathDir.isDirectory()){
                        System.out.println(TAG + "...made thumb dir: " + thumbPathDir);
                        thumbPathDir.mkdirs();
                    }
                }
            }
            
        }// end try
        catch(FileNotFoundException ex){
            System.out.println(TAG + "...in catch");
            ex.printStackTrace();
        }
        catch(Exception ex){ ex.printStackTrace();}
        
               
        //call the Scaler function class and scale the pictures here.
        //call write on the scaled BufferedImage. Save as same file type as uploaded image.
        if (fileFound == true) {
            try{
                System.out.println("UploadHandler-->ScalerCall-->1st try");
                BufferedImage thePic = ImageIO.read(tempFile);
                Scaler scaler = new Scaler();
                BufferedImage profileImg = scaler.getScaledInstance(thePic, 150);
                BufferedImage profileThumb = scaler.getScaledInstance(thePic, 60);
                File profPic = new File(imgPath, fileName);
                File thumbPic = new File(thumbPath, fileName);
                ImageIO.write(profileImg, extension, profPic);
                ImageIO.write(profileThumb, extension, thumbPic);
                System.out.println("UploadHandler-->ScalerCall-->1st try: after writes.");
            }
            catch(Exception ex) {ex.printStackTrace();}

            // update pic to the database and retrieve updated Person object
            Update up = new Update();
            String picString = "profile/" + user.getId() +
                                "/images/" + fileName;
            System.out.println("UploadHandler->picString: " + picString);
            up.updater(datasource, email, picString);
            
            // delete the tempFile
            if(tempFile.exists()){
                tempFile.delete();
            }
        }
        
    } //end doPost  
}