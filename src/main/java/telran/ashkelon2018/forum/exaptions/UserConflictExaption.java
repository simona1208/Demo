package telran.ashkelon2018.forum.exaptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserConflictExaption extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
