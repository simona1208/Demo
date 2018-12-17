package telran.ashkelon2018.forum.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder 
public class NewPostDto {
	
	
	String title;
	String content;
	Set<String> tags;
	
	
	

}
