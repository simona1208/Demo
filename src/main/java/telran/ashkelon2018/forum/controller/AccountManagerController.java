package telran.ashkelon2018.forum.controller;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredential;
import telran.ashkelon2018.forum.dao.UserAccRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exaptions.PermissionExp;
import telran.ashkelon2018.forum.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountManagerController {
	@Autowired
	AccountService accountService;
	@Autowired
	AccountConfiguration configuration;
	@Autowired
	UserAccRepository repository;
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletResponse response;

	@PostMapping // ANY
	public UserProfileDto register(@RequestBody UserRegDto userRegDto, @RequestHeader("Authorization") String token) {

		return accountService.addUser(userRegDto, token);
	}

	@PutMapping("/update") // OWNER
	public UserProfileDto update(@RequestBody UserRegDto userRegDto, @RequestHeader("Authorization") String token)
			throws IOException {

		UserAccount userAccount = doCheck(request, response);

		if (userAccount != null) {

			if (!userAccount.getRoles().contains("User")) {
				response.sendError(403, "It's not your's account");

			}
		} else {
			return accountService.editUser(userRegDto, token);

		}

		return null;
	}

	@DeleteMapping("/{login}") // OWNER, moderator, ADMIN
	public UserProfileDto remove(@PathVariable String login, @RequestHeader("Authorization") String token) throws IOException {
		
		UserAccount userAccount = doCheck(request, response);

		if (userAccount != null) {

			if   (!(  userAccount.getRoles().contains("User")
					||userAccount.getRoles().contains("Admin")
					||userAccount.getRoles().contains("Moderator"))) {
				
				throw new PermissionExp();
			}
		} else {
			return accountService.removeUser(login, token);

		}

		return null;
		
	}

	@PutMapping("/role/{login}/{role}") // ADMIN
	public Set<String> addRole(@PathVariable String login, @PathVariable String role,
			@RequestBody UserRegDto userRegDto, @RequestHeader("Authorization") String token) throws IOException {

		UserAccount userAccount = doCheck(request, response);

		if (userAccount != null) {

			if (!userAccount.getRoles().contains("Admin")) {
				response.sendError(403, "It's not your's account");

			}
		} else {
			return accountService.addRole(login, role, token);
		}

		return null;		
		
	}

	@DeleteMapping("/role/{login}/{role}") // ADMIN
	public Set<String> removeRole(@PathVariable String login, @PathVariable String role,
			@RequestBody UserRegDto userRegDto
			, @RequestHeader("Authorization") String token) throws IOException {

		UserAccount userAccount = doCheck(request, response);

		if (userAccount != null) {

			if (!userAccount.getRoles().contains("Admin")) {
				response.sendError(403, "It's not your's account");

			}
		} else {
			return accountService.removeRole(login, role, token);
		}

		return null;
				
	}

	@PutMapping("/password") // OWNER
	public void changePassword(@RequestHeader("Authorization") String password,
			@RequestHeader("Authorization") String token) throws IOException {

		UserAccount userAccount = doCheck(request, response);

		if (userAccount != null) {

			if (!userAccount.getRoles().contains("User")) {
				response.sendError(403, "It's not your's account");

			}
		} else {
			accountService.changePass(password, token);

		}		
		
	}

	@GetMapping // ANY
	public UserProfileDto getUsersAccount(@RequestHeader("Authorization") String token) {

		return accountService.getUser(token);
	}

	private UserAccount doCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String token = request.getHeader("Authorization");

		if (token == null) {
			response.sendError(401, "Unauthorized");
			return null;
		}

		AccountUserCredential userCredential = null;

		try {
			userCredential = configuration.tokenDecode(token);
		} catch (Exception e) {
			response.sendError(401, "Unauthorized");
			return null;
		}
		UserAccount userAccount = repository.findById(userCredential.getLogin()).orElse(null);

		if (userAccount == null) {
			response.sendError(401, "User not Found");
			return null;
		}

		return userAccount;

	}

}
