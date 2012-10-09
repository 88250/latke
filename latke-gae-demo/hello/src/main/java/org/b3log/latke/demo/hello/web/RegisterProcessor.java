package org.b3log.latke.demo.hello.web;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.demo.hello.repository.UserRepository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.annotation.RequestProcessing;
import org.b3log.latke.servlet.annotation.RequestProcessor;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.renderer.freemarker.FreeMarkerRenderer;
import org.b3log.latke.util.Strings;
import org.json.JSONObject;

/**
 * Register.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 9, 2012
 */
@RequestProcessor
public final class RegisterProcessor {

    private static final Logger LOGGER = Logger.getLogger(RegisterProcessor.class.getName());
    /**
     * User repository.
     */
    private UserRepository userRepository = new UserRepository("user repository");

    @RequestProcessing(value = "/register", method = {HTTPRequestMethod.GET, HTTPRequestMethod.POST})
    public void register(final HTTPRequestContext context, final HttpServletRequest request) {
        final AbstractFreeMarkerRenderer render = new FreeMarkerRenderer();
        context.setRenderer(render);

        render.setTemplateName("register.ftl");
        final Map<String, Object> dataModel = render.getDataModel();

        final String name = request.getParameter("name");
        if (!Strings.isEmptyOrNull(name)) {
            LOGGER.log(Level.FINER, "Name[{0}]", name);
            dataModel.put("name", name);

            saveUser(name);
        }
    }

    private void saveUser(final String name) {
        final Transaction transaction = userRepository.beginTransaction();
        try {
            final JSONObject user = new JSONObject();
            user.put("name", name);

            final String userId = userRepository.add(user);

            transaction.commit();

            LOGGER.log(Level.INFO, "Register a user successfully[userId={0}]", userId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            // Just logging here....
            LOGGER.log(Level.SEVERE, "Can not register user", e);
        }
    }
}
