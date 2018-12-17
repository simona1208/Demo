package telran.ashkelon2018.forum.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {
	
	
	List<Post> findByAuthor(String author);
	
	//@Query("{'tags':{$in:?0}}")
	Iterable<Post> findByTagsIn(List<String> tags);
	
	List<Post> findByDateCreatedBetween(LocalDate from, LocalDate to);
}