package telran.ashkelon2018.forum.service;

import java.util.Set;


import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.dto.UserProfileDto;

public interface AccountService {

	UserProfileDto addUser(UserRegDto userregDto, String token);

	UserProfileDto editUser(UserRegDto userregDto, String token);
	
	UserProfileDto getUser(String token);

	UserProfileDto removeUser(String login, String token);

	Set<String> addRole(String login, String role, String token);
	Set<String> removeRole(String login, String role, String token);

	boolean changePass(String password, String token);
}
