package charron.projetprimejsp.Services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionManager<T>
{
    private HttpSession session;
    @Autowired
    public SessionManager(HttpSession session)
    {
        this.session = session;
    }
    public T getAttribute(String keyName)
    {
        return (T)session.getAttribute(keyName);
    }
    public void setAttribute(String keyName, T value)
    {
        session.setAttribute(keyName, value);
    }
    public void removeAttribute(String keyName){session.removeAttribute(keyName);}
    public void clearAll(){session.invalidate();}
}
