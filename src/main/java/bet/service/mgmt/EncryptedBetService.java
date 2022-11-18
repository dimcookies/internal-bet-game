package bet.service.mgmt;

import bet.api.constants.OverResult;
import bet.api.constants.ScoreResult;
import bet.api.dto.EncryptedBetDto;
import bet.model.Bet;
import bet.model.EncryptedBet;
import bet.model.Game;
import bet.model.User;
import bet.repository.BetRepository;
import bet.repository.DeadlineRepository;
import bet.repository.EncryptedBetRepository;
import bet.repository.GameRepository;
import bet.service.email.EmailSender;
import bet.service.encrypt.EncryptHelper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EncryptedBetService extends AbstractManagementService<EncryptedBet, Integer, EncryptedBetDto> {

//	@Value("${application.allowedMatchDays}")
//	private String[] allowedMatchDays;

	@Autowired
	private EncryptHelper encryptHelper;

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private EncryptedBetRepository encryptedBetRepository;

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private DeadlineRepository deadlineRepository;


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

	/**
	 * Encrypt a bet
	 * @param dto
	 */
	private void encryptBets(EncryptedBetDto dto) {
		String salt = getSalt(dto);
		try {
			if(dto.getOverResult() != null) {
				dto.setOverResult(encryptHelper.encrypt(dto.getOverResult(), salt));
			}
			if(dto.getScoreResult() != null) {
				dto.setScoreResult(encryptHelper.encrypt(dto.getScoreResult(), salt));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Decrypt a bet
	 * @param dto
	 */
	private void decryptBets(EncryptedBetDto dto) {
		String salt = getSalt(dto);
		try {
			if(dto.getOverResult() != null) {
				dto.setOverResult(encryptHelper.decrypt(dto.getOverResult(), salt));
			}
			dto.setScoreResult(encryptHelper.decrypt(dto.getScoreResult(), salt));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the salf for the encryption
	 * @param dto
	 * @return
	 */
	private String getSalt(EncryptedBetDto dto) {
		String userId = dto.getUserId().toString();
		String gameId = dto.getGameId().toString();

		//10 first characters of userId and 10 first characters of gameId
		return userId.substring(0,Math.min(10, userId.length())) + gameId.substring(0,Math.min(10, gameId.length()));
	}

	@Override
	public EncryptedBetDto update(EncryptedBetDto dto) {
		encryptBets(dto);
		return super.create(dto);
	}

	/**
	 * Decrypt encrypted bets, copy them to public available bets and delete encrypted
	 */
	public void decryptAndCopy() {
		list().forEach(dto -> {
			//create a public available bet from encrypted bet values
			Bet bet = new Bet(null, dto.getGameId(), dto.getUserId(), ScoreResult.valueOf(dto.getScoreResult()),
					0, dto.getOverResult() != null ? OverResult.valueOf(dto.getOverResult()) : null, 0, ZonedDateTime.parse(dto.getBetDate()));
			betRepository.save(bet);
			//delete encrypted bets
			repository.delete(dto.getId());
		});
	}

	/**
	 * Get encrypted bets for a user
	 *
	 * @param user
	 * @return
	 */
	public List<EncryptedBetDto> list(User user) {
		return list().stream().filter(encryptedBetDto -> encryptedBetDto.getUserId()
				.equals(user.getId())).collect(Collectors.toList());
	}

	/**
	 * Update a list of encrypted bets (delete existing before saving current)
	 * @param bets
	 * @param user
	 * @return
	 */
	@Transactional
	public List<EncryptedBetDto> createAll(List<EncryptedBetDto> bets, User user) {
		ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
		String allowedMatchDays = deadlineRepository.findActiveDeadline(now, now)
				.getAllowedMatchDays();
		if(StringUtils.isEmpty(allowedMatchDays)) {
			throw new RuntimeException("User " + user.getUsername() + " tried to create bet while not allowed. " +
					                           "Whole request:" + bets);
		}
		//the configured days that are allowed to get bets for
		List<Integer> allowedDays = Arrays.stream(allowedMatchDays.split(","))
				.map(Integer::parseInt).collect(Collectors.toList());

		long matchNum = allowedDays.stream()
				.mapToLong(day -> gameRepository.findByMatchDay(day).size())
				.sum();

		//check if a provided bet is for a game outside allowed days
		bets.forEach(encryptedBetDto -> {
			Game game = gameRepository.findOne(encryptedBetDto.getGameId());
			if(allowedDays.indexOf(game.getMatchDay()) == -1) {
				throw new RuntimeException("User " + user.getUsername() + " tried to create not allowed game:" + game + " Whole request:" + bets + " Allowed days:" + allowedDays);
			}
		});

		List<EncryptedBetDto> validBets
				= bets.stream()
				.filter(encryptedBetDto -> StringUtils.isNotBlank(encryptedBetDto.getScoreResult()))
				.collect(Collectors.toList());

		String title = "World Cup Challenge 2022 Bet";
		if(validBets.size() < matchNum) {
			title = "(INCOMPLETE!!!) " + title + " (INCOMPLETE!!!)";
		}

		Context context = new Context();
		context.setVariable("bets", getMailBets(validBets));
		context.setVariable("betsNum", validBets.size());
		context.setVariable("matchNum", matchNum);
		context.setVariable("title", title);
		String html = templateEngine.process("email-bet", context);

		//delete current bets
		encryptedBetRepository.deleteByUser(user);

		List<EncryptedBetDto> result = validBets.stream().map(encryptedBetDto -> {
			encryptedBetDto.setId(null);
			//set user of bet to current user
			encryptedBetDto.setUserId(user.getId());
			//set bet date to now
			encryptedBetDto.setBetDate(now.toString());
			return encryptedBetDto;
		}).map(encryptedBetDto -> create(encryptedBetDto)).collect(Collectors.toList()); //save bet and return list

		//Send an email to user with saved bets
		emailSender.sendEmail(user.getEmail(), title, html, true);

		return result;
	}

	/**
	 * Format bets to an html table
	 * @param bets
	 * @return
	 */
	private List<Map<String,Object>> getMailBets(List<EncryptedBetDto> bets) {
		return bets.stream()
				.map(bet -> {
					Game game = gameRepository.findOne(bet.getGameId());
					return new HashMap<String, Object>() {{
						put("game", game);
						put("bet", bet);
					}};
				})
				.sorted((o1, o2) -> {
					int res = ((Game) o1.get("game")).getGameDate().compareTo(((Game) o2.get("game")).getGameDate());
					if(res != 0) {
						return res;
					}
					return ((Game) o1.get("game")).getId().compareTo(((Game) o2.get("game")).getId());
				})
				.collect(Collectors.toList());
	}
}
