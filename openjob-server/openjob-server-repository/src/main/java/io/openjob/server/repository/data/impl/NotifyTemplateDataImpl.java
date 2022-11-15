package io.openjob.server.repository.data.impl;

import io.openjob.server.repository.dao.NotifyTemplateDAO;
import io.openjob.server.repository.data.NotifyTemplateData;
import io.openjob.server.repository.dto.NotifyTemplateDTO;
import io.openjob.server.repository.entity.NotifyTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @author inhere
 * @date 2022-11-14 20:39:14
 * @since 1.0.0
 */
@Component
public class NotifyTemplateDataImpl implements NotifyTemplateData {

    private final NotifyTemplateDAO notifyTemplateDAO;
    // private final RedisOperation redisOperation;

    @Autowired
    public NotifyTemplateDataImpl(NotifyTemplateDAO notifyTemplateDAO) {
        this.notifyTemplateDAO = notifyTemplateDAO;
    }

    @Override
    public Long add(NotifyTemplateDTO dto) {
        NotifyTemplate entity = new NotifyTemplate();
        BeanUtils.copyProperties(dto, entity);

        return notifyTemplateDAO.add(entity);
    }

    @Override
    public void batchAdd(List<NotifyTemplateDTO> dtoList) {
        List<NotifyTemplate> entityList = new ArrayList<>();
        // TODO copy data

        notifyTemplateDAO.batchAdd(entityList);
    }

    @Override
    public NotifyTemplateDTO getById(Long id) {
        NotifyTemplate entity = notifyTemplateDAO.getById(id);
        NotifyTemplateDTO entDTO = new NotifyTemplateDTO();
        BeanUtils.copyProperties(entity, entDTO);

        return entDTO;
    }

    @Override
    public Integer updateById(NotifyTemplateDTO dto) {
        NotifyTemplate entity = new NotifyTemplate();
        BeanUtils.copyProperties(dto, entity);

        return notifyTemplateDAO.updateById(entity);
    }

    @Override
    public List<NotifyTemplateDTO> getPageList(Long id) {
        // TODO
        return null;
    }
}
