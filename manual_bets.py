import sys

user_id = 211
with open(sys.argv[1]) as f:
    for line in f:
        line = line.strip()
        ar = line.split(",")
        t='HOME_1'
        if ar[1] == 'X':
            t='DRAW_X'
        elif ar[1] == '2':
            t='AWAY_2'
        print("""insert into bet.bet (game_id,user_id,result_bet,result_points,over_points,bet_date) values ('%s',%d,'%s',0,0,now());""" %(user_id,ar[0],t))  
