package com.grooble.web;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;

import java.io.*;

@SuppressWarnings("serial")
public class ImageStreamer  extends HttpServlet
{
    public void doGet(HttpServletRequest  request, HttpServletResponse response)
			     throws ServletException, IOException
    {
        // Parse URI to find the filename
        String uri = request.getRequestURI();
		String[] tokens = uri.split("/");
		String filename = "";

		for(int i = 2; i < tokens.length; i++){
            filename += ("/" + tokens[i]);
        }
            
        // Set Content type
        String contentType = getServletContext().getMimeType(filename);

        // Stream the image
        BufferedInputStream in = null;
        try
        {
            in = new BufferedInputStream(
		getServletContext().
                    getResourceAsStream("/WEB-INF/" + filename));

            response.setContentType(contentType);
            response.setHeader("Content-Disposition",  " inline; filename=" + filename);

            ServletOutputStream out = response.getOutputStream();
            
            byte[] buffer = new byte[4 * 1024];

            int data;
            while((data = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, data);
            }
            out.flush();
        }
        catch(Exception e)
        {
			e.printStackTrace();
		return;
        }
        finally
        {
            try
            {
                in.close();
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        }
    }        
}
