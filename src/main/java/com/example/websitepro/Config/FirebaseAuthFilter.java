package com.example.websitepro.Config;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String idToken = authorizationHeader.substring(7);
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

                QueryDocumentSnapshot document = getUserByUid(decodedToken.getUid());
                List<String> roles = (List<String>) document.get("userRoles");

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(decodedToken.getEmail(), null, getAuthorities(roles));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (FirebaseAuthException e) {
                e.printStackTrace();
                throw new WebException(Constant.MESSAGE.JWT_WRONG);
            } catch (RuntimeException e){
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roleList) {
        return roleList.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }

    private QueryDocumentSnapshot getUserByUid(String uid){
        try {
            ApiFuture<QuerySnapshot> user = FirestoreClient.getFirestore().collection("users").whereEqualTo("uid", uid).get();
            QuerySnapshot query = user.get();
            QueryDocumentSnapshot document = query.getDocuments().get(0);
            return document;
        }catch (RuntimeException e){
            e.printStackTrace();
            throw new WebException("ERROR_OCCURRED");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new WebException("ERROR_OCCURRED");
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new WebException("ERROR_OCCURRED");
        }
    }

}
