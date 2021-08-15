package edu.bu.cs673.stockportfolio.api.exception;

import edu.bu.cs673.stockportfolio.service.utilities.ResponseService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**********************************************************************************************************************
 * The FileUploadExceptionController class provides custom html page for events causing an HTTP 403 error.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
@ControllerAdvice
public class FileUploadExceptionController {
    private final ResponseService responseService;

    public FileUploadExceptionController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(Model model) {
        return responseService.createExceedFileSizeError(true, model);
    }
}