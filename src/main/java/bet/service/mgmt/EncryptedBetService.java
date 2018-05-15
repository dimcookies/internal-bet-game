package bet.service.mgmt;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.BetDto;
import bet.api.dto.EncryptedBetDto;
import bet.api.dto.GameDto;
import bet.api.dto.OddDto;
import bet.model.Bet;
import bet.model.EncryptedBet;
import bet.model.Game;
import bet.model.Odd;
import bet.repository.BetRepository;
import bet.service.utils.EncryptUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncryptedBetService extends AbstractManagementService<EncryptedBet, Integer, EncryptedBetDto> {

	@Autowired
	private EncryptUtils encryptUtils;

	@Autowired
	private BetRepository betRepository;

	@Override
	public List<EncryptedBetDto> list() {
		return Lists.newArrayList(repository.findAll()).stream().map(entity -> {
			EncryptedBetDto dto = new EncryptedBetDto();
			dto.fromEntity(entity);
			decryptBets(dto);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public EncryptedBetDto create(EncryptedBetDto dto) {
		encryptBets(dto);
		return super.create(dto);
	}

	private void encryptBets(EncryptedBetDto dto) {
		try {
			dto.setOverResult(encryptUtils.encrypt(dto.getOverResult(), dto.getUserId().toString()));
			dto.setScoreResult(encryptUtils.encrypt(dto.getScoreResult(), dto.getUserId().toString()));
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private void decryptBets(EncryptedBetDto dto) {
		try {
			dto.setOverResult(encryptUtils.decrypt(dto.getOverResult(), dto.getUserId().toString()));
			dto.setScoreResult(encryptUtils.decrypt(dto.getScoreResult(), dto.getUserId().toString()));
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public EncryptedBetDto update(EncryptedBetDto dto) {
		encryptBets(dto);
		return super.create(dto);
	}

	public void decryptAndCopy() {
		list().forEach(dto -> {
			Bet bet = new Bet(null, dto.getGameId(), dto.getUserId(), ScoreResult.valueOf(dto.getScoreResult()),
					0, OverResult.valueOf(dto.getOverResult()), 0);
			betRepository.save(bet);
			repository.delete(dto.getId());
		});
	}
}
