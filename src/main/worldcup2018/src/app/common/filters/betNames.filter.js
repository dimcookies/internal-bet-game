export default function BetNames() {
	return function(val) {
		let friendlyName = val;
		switch (val) {
			case 'HOME_1':
				friendlyName = '1';
				break;
			case 'DRAW_X':
				friendlyName = 'X';
				break;
			case 'AWAY_2':
				friendlyName = '2';
				break;
			case 'OVER':
				friendlyName = 'OVER';
				break;
			case 'UNDER':
				friendlyName = 'UNDER';
				break;
		}
		return friendlyName;
	};
}

BetNames.$inject = [];