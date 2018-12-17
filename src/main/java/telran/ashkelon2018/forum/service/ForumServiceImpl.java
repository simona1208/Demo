package telran.ashkelon2018.forum.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredential;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;

@Service


public class ForumServiceImpl implements ForumService {

	@Autowired
	AccountConfiguration AccountConfiguration;
	
	@Autowired
	ForumRepository repository;
	@Autowired
	UserAccRepository accRepository;
	@Autowired
	AccountConfiguration configuration;

	@Override
	public Post addNewPost(NewPostDto newPost, String token) {
		
		AccountUserCredential userCredential = configuration.tokenDecode(token);
		UserAccount userAccount = accRepository.findById(userCredential.getLogin()).orElse(null);

		String author = userAccount.getLogin();
			
		
			Post post = new Post(newPost.getTitle(), newPost.getContent(), author, newPost.getTags());
			repository.save(post);
			return post;
	
		
	}

	@Override
	public Post getPost(String id) {

		return repository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String token) {

		AccountUserCredential userCredential = configuration.tokenDecode(token);
		UserAccount userAccount = accRepository.findById(userCredential.getLogin()).orElse(null);

		String author = userAccount.getLogin();
		Set<String> roles = userAccount.getRoles();
		
		
		boolean owner 	 = author.equals(userCredential.getLogin());
		boolean hasRight = roles.stream().anyMatch(s -> "Admin".equals(s)||"Moderator".equals(s));
				
				
		if(owner||hasRight  ) {
			
			
		Post post = repository.findById(id).orElse(null);
		repository.delete(post);
		return post;
		}
		
		return null;
	}

	@Override
	public Post updatePOst(PostUpdateDto postUpd, String token) {
		
				
		AccountUserCredential userCredential = configuration.tokenDecode(token);
		UserAccount userAccount = accRepository.findById(userCredential.getLogin()).orElse(null);

		String author = userAccount.getLogin();
			
		boolean owner 	 = author.equals(userCredential.getLogin());
						
		if(owner) {	
			
			Post post = repository.findById(postUpd.getId()).orElse(null);

			post.setContent(postUpd.getContent());
			post.setTitle(postUpd.getTitle());
			post.setTags(postUpd.getTags());
			repository.save(post);

			return post;
		}
		
		return null;
		

		
	}

	@Override
	public boolean addLike(String id) {
		Post post = repository.findById(id).orElse(null);

		if (post == null) {
			return false;
		}
		post.addLike();
		repository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment, String token) {
		
		
		
		Post post = repository.findById(id).orElse(null);
		Comment comment = new Comment(newComment.getUser(), newComment.getMessage());

		post.addComment(comment);
		repository.save(post);
		return post;
	}

	@Override
	public Iterable<Post> findPostsByTag(List<String> tags) {

		return repository.findByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findPostsByAuthor(String author) {
		return repository.findByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostsByDates(DatePeriodDto period) {

		LocalDate from = period.getFrom();
		System.out.println(from);
		LocalDate to = period.getTo();
		System.out.println(to);

		
		return repository.findByDateCreatedBetween(from, to);
	}

}
