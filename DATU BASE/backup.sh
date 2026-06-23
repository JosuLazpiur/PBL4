FECHA=$(date +%F_%H-%M)
DESTINO="./backups" 
BASEDEDATOS="gertuko"
USUARIO="root"
PASSWORD="pasahitza"

mysqldump -h mysql -P 3306 -u $USUARIO -p$PASSWORD $BASEDEDATOS > $DESTINO/$BASEDEDATOS-$FECHA.sql
