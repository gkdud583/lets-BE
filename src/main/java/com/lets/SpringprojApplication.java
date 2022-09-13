package com.lets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //JPA Auditing 활성화
@SpringBootApplication
public class SpringprojApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringprojApplication.class, args);
    //		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
    //
    //		EntityManager em = emf.createEntityManager();
    //
    //
    //		EntityTransaction tx = em.getTransaction();
    //
    //		tx.begin();
    //
    //		//==member 저장 테스트==//
    //		Tag tag = Tag.createTag("spring");
    //		em.persist(tag);
    //
    //
    //		List<UserTechStack> userTechStackList = new ArrayList<>();
    //		UserTechStack userTechStack = UserTechStack.createUserTechStack(tag);
    //		userTechStackList.add(userTechStack);
    //
    //		User user = User.createUser("user1", "aaqq5533@hanmail.net", "github", userTechStackList);
    //
    //
    //		em.persist(user);

    //		//==post 저장 테스트==//
    //		Tag tag = Tag.createTag("spring");
    //		em.persist(tag);
    //
    //		List<UserTechStack> userTechStackList = new ArrayList<>();
    //		UserTechStack memberTechStack = UserTechStack.createMemberTechStack(tag);
    //		userTechStackList.add(memberTechStack);
    //
    //		User user = User.createUser("user1", userTechStackList);
    //
    //		em.persist(user);
    //
    //		List<PostTechStack> postTechStacks = new ArrayList<>();
    //		PostTechStack postTechStack = PostTechStack.createPostTechStack(tag);
    //
    //		postTechStacks.add(postTechStack);
    //		Post post = Post.createPost(user, "title1", "content1", postTechStacks);
    //
    //		em.persist(post);
    //
    //
    //		em.flush();
    //		em.clear();
    //
    //		Tag findTag = em.find(Tag.class, tag.getId());
    //		Post findPost = em.find(Post.class, post.getId());
    //		System.out.println("tag.getPostTechStacks().size() = " + findTag.getPostTechStacks().size());
    //		System.out.println("post.getPostTechStacks() = " + findPost.getPostTechStacks().size());

    //==comment 저장 테스트==//
    //		Tag tag = Tag.createTag("spring");
    //		em.persist(tag);
    //
    //		List<UserTechStack> userTechStackList = new ArrayList<>();
    //		UserTechStack userTechStack = UserTechStack.createUserTechStack(tag);
    //		userTechStackList.add(userTechStack);
    //
    //		User user = User.createUser("user1", userTechStackList);
    //
    //		em.persist(user);
    //
    //		List<PostTechStack> postTechStacks = new ArrayList<>();
    //		PostTechStack postTechStack = PostTechStack.createPostTechStack(tag);
    //
    //		postTechStacks.add(postTechStack);
    //		Post post = Post.createPost(user, "title1", "content1", postTechStacks);
    //
    //		em.persist(post);
    //
    //		Comment comment = Comment.createComment(user, post, "comment1");
    //
    //		em.persist(comment);
    //
    //
    //		em.flush();
    //		em.clear();
    //
    //		Post findPost = em.find(Post.class, post.getId());
    //		System.out.println("findPost.getComments().size() = " + findPost.getComments().size());

    //		//==LikePost 테스트==//
    //		Tag tag = Tag.createTag("spring");
    //		em.persist(tag);
    //
    //		List<UserTechStack> userTechStackList = new ArrayList<>();
    //		UserTechStack userTechStack = UserTechStack.createUserTechStack(tag);
    //		userTechStackList.add(userTechStack);
    //
    //		User user = User.createUser("user1", userTechStackList);
    //
    //		em.persist(user);
    //
    //		List<PostTechStack> postTechStacks = new ArrayList<>();
    //		PostTechStack postTechStack = PostTechStack.createPostTechStack(tag);
    //
    //		postTechStacks.add(postTechStack);
    //		Post post = Post.createPost(user, "title1", "content1", postTechStacks);
    //
    //		em.persist(post);
    //
    //		LikePost likePost = LikePost.createLikePost(user, post);
    //
    //		em.persist(likePost);
    //
    //		System.out.println("member.getLikePosts().size() = " + user.getLikePosts().size());

    //==좋아요 클릭 테스트==//
    //		Tag tag = Tag.createTag("spring");
    //		em.persist(tag);
    //
    //		List<UserTechStack> userTechStackList = new ArrayList<>();
    //		UserTechStack userTechStack = UserTechStack.createUserTechStack(tag);
    //		userTechStackList.add(userTechStack);
    //
    //		User user = User.createUser("user1", userTechStackList);
    //
    //		em.persist(user);
    //
    //		List<PostTechStack> postTechStacks = new ArrayList<>();
    //		PostTechStack postTechStack = PostTechStack.createPostTechStack(tag);
    //
    //		postTechStacks.add(postTechStack);
    //		Post post = Post.createPost(user, "title1", "content1", postTechStacks);
    //
    //		em.persist(post);
    //
    //		LikePost likePost = LikePost.createLikePost(user, post);
    //
    //		em.persist(likePost);
    //
    //
    //
    //
    //
    //		likePost.activateLike();
    //
    //		em.flush();
    //		em.clear();
    //
    //		LikePost findLikePost = em.find(LikePost.class, likePost.getId());
    //		Post findPost = em.find(Post.class, post.getId());
    //
    //		System.out.println("findLikePost.getStatus() = " + findLikePost.getStatus());
    //		System.out.println("findPost.getLikeCount() = " + findPost.getLikeCount());
    //		System.out.println("findPost.getViewCount() = " + findPost.getViewCount());
    //
    //
    //		System.out.println("======취소 테스트======");
    //
    //		findLikePost.deactivateLike();
    //
    //		em.flush();
    //		em.clear();
    //
    //		findLikePost = em.find(LikePost.class, likePost.getId());
    //		findPost = em.find(Post.class, post.getId());
    //
    //		System.out.println("findLikePost.getStatus() = " + findLikePost.getStatus());
    //		System.out.println("findPost.getLikeCount() = " + findPost.getLikeCount());
    //		System.out.println("findPost.getViewCount() = " + findPost.getViewCount());

    //		tx.commit();
    //		em.close();
    //		emf.close();
    //

  }
}
