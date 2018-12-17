package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredential;
import telran.ashkelon2018.forum.dao.UserAccRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exaptions.UserConflictExaption;

@Service

public class AccountServiseImpl implements AccountService {

	@Autowired
	UserAccRepository userRepository;
	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userregDto, String token) {

		AccountUserCredential credential = accountConfiguration.tokenDecode(token);

		if (userRepository.existsById(credential.getLogin())) {

			throw new UserConflictExaption();

		}

		String hashPassword = BCrypt.hashpw(credential.getPassword(), BCrypt.gensalt());

		UserAccount userAccount = UserAccount.builder().login(credential.getLogin()).password(hashPassword)
				.firstName(userregDto.getFirstName()).lastName(userregDto.getLastName()).role("User")
				.expDate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod())).build();

		userRepository.save(userAccount);

		return convertToUserProfileDto(userAccount);
	}

	private UserProfileDto convertToUserProfileDto(UserAccount userAccount) {

		UserProfileDto accountDto = UserProfileDto.builder().firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName()).login(userAccount.getLogin()).roles(userAccount.getRoles())
				.build();

		return accountDto;
	}

	@Override
	public UserProfileDto editUser(UserRegDto userregDto, String token) {
		
		AccountUserCredential credential = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credential.getLogin()).get();

		if (userregDto.getFirstName() != null) {
			userAccount.setFirstName(userregDto.getFirstName());
		}

		if (userregDto.getLastName() != null) {
			userAccount.setLastName(userregDto.getLastName());
		}

		userRepository.save(userAccount);

		return convertToUserProfileDto(userAccount);

	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		
		AccountUserCredential userCredential = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(userCredential.getLogin()).orElse(null);
		// FIXME

		if (userAccount.getRoles().contains("admin") || userAccount.getRoles().contains("moderator")) {
			
		}
		
		if (userAccount != null) {

			userRepository.delete(userAccount);

		}
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {

		// FIXME
		UserAccount userAccount = userRepository.findById(login).orElse(null);

		if (userAccount != null) {

			userAccount.addRole(role);
			userRepository.save(userAccount);

		} else {
			return null;
		}

		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		UserAccount userAccount = userRepository.findById(login).orElse(null);

		// FIXME
		if (userAccount != null) {

			userAccount.removeRole(role);
			userRepository.save(userAccount);

		} else {

			return null;
		}

		return userAccount.getRoles();

	}

	@Override
	public boolean changePass(String password, String token) {

		AccountUserCredential credential = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credential.getLogin()).get();

		String hashpassword = BCrypt.hashpw(password, BCrypt.gensalt());
		userAccount.setPassword(hashpassword);
		userAccount.setExpDate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()));
		userRepository.save(userAccount);
		return false;
	}

	@Override
	public UserProfileDto getUser(String token) {

		AccountUserCredential credential = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credential.getLogin()).get();

			return convertToUserProfileDto(userAccount);

	

	}

}
