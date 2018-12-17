package telran.ashkelon2018.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.service.ForumService;


@RestController
@RequestMapping("/forum")
public class ForumController {

	
	@Autowired
	ForumService forumService;
	
	@PostMapping("/post")
	Post addNewPost(@RequestBody NewPostDto newPost, @RequestHeader("Authorization") String token) {
		
		return forumService.addNewPost(newPost, token);
		
	}

	@GetMapping("/post/{id}")
	Post getPost(@PathVariable String id) {
		return forumService.getPost(id);
	}

	
	@DeleteMapping("/post/{id}")    // owner moderator admin
	Post removePost(@PathVariable String id, @RequestHeader("Authorization") String token) {
		return forumService.removePost(id,token);
	}

	@PutMapping("/post/{id}")  // owner
	Post updatePOst(@RequestBody PostUpdateDto postUpd,  @RequestHeader("Authorization") String token) {
		return forumService.updatePOst(postUpd,token);
	}

	@PutMapping("/post/likes/{id}") // user who is not contained in UserSet
	boolean addLike(@PathVariable String id) {
		
		return forumService.addLike(id);
	}
	
	
	@PostMapping("/post/{id}/comment") // any  user 
	Post addComment(@PathVariable String id, @RequestBody NewCommentDto newComment, @RequestHeader("Authorization") String token) {
		return forumService.addComment(id, newComment,token);
	}
	
	@GetMapping("/posts")  // 
	Iterable<Post> findPostsByTag (@RequestParam List<String> tags) {
		return forumService.findPostsByTag(tags);
	}
	
	@GetMapping("/posts/author/{author}")
	Iterable<Post> findPostsByAuthor (@PathVariable String author) {
		return forumService.findPostsByAuthor(author);  //author
	}
	
	@PostMapping("/posts/period")
	Iterable<Post> findPostsByDates (@RequestBody DatePeriodDto period) {
		
		System.out.println(period.toString());

		return forumService.findPostsByDates(period);
	}
	
	
	

}
