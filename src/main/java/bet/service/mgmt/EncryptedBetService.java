package bet.service.mgmt;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.BetDto;
import bet.api.dto.EncryptedBetDto;
import bet.api.dto.GameDto;
import bet.api.dto.OddDto;
import bet.model.*;
import bet.repository.BetRepository;
import bet.repository.EncryptedBetRepository;
import bet.repository.GameRepository;
import bet.repository.UserRepository;
import bet.service.email.EmailSender;
import bet.service.utils.EncryptUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncryptedBetService extends AbstractManagementService<EncryptedBet, Integer, EncryptedBetDto> {

	@Autowired
	private EncryptUtils encryptUtils;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private EncryptedBetRepository encryptedBetRepository;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

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
			if(dto.getOverResult() != null) {
				dto.setOverResult(encryptUtils.encrypt(dto.getOverResult(), dto.getUserId().toString()));
			}
			dto.setScoreResult(encryptUtils.encrypt(dto.getScoreResult(), dto.getUserId().toString()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void decryptBets(EncryptedBetDto dto) {
		try {
			if(dto.getOverResult() != null) {
				dto.setOverResult(encryptUtils.decrypt(dto.getOverResult(), dto.getUserId().toString()));
			}
			dto.setScoreResult(encryptUtils.decrypt(dto.getScoreResult(), dto.getUserId().toString()));
		} catch (Exception e) {
			throw new RuntimeException(e);
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
					0, dto.getOverResult() != null ? OverResult.valueOf(dto.getOverResult()) : null, 0);
			betRepository.save(bet);
			repository.delete(dto.getId());
		});
	}

	@Transactional
	public List<EncryptedBetDto> createAll(List<EncryptedBetDto> bets, User user) {
		encryptedBetRepository.deleteByUser(user);
		String body = String.format("<html><body><table border=1>%s</table></body></html>", getEmailBody(bets));
		emailSender.sendEmail(user.getEmail(), "WC2018 Bet", body);

		return bets.stream().map(encryptedBetDto -> {
			encryptedBetDto.setUserId(user.getId());
			return encryptedBetDto;
		}).map(encryptedBetDto -> create(encryptedBetDto)).collect(Collectors.toList());
	}

	private String getEmailBody(List<EncryptedBetDto> bets) {
		return bets.stream().map(bet -> {
			Game game = gameRepository.findOne(bet.getGameId());
			return String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",game.getHomeName(), game.getAwayName(),
					bet.getScoreResult(), bet.getOverResult());
		}).collect(Collectors.joining());
	}
}
