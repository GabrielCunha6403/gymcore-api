do $$
begin
    if to_regclass('public.professor') is not null then
        if not exists (
            select 1 from information_schema.columns
            where table_name = 'professor' and column_name = 'status'
        ) then
            alter table professor add column status varchar(30);
        end if;

        if exists (
            select 1 from information_schema.columns
            where table_name = 'professor' and column_name = 'ativo'
        ) then
            update professor
            set status = case when ativo is true then 'ATIVO' else 'INATIVO' end
            where status is null;

            alter table professor drop column ativo;
        end if;

        update professor set status = 'ATIVO' where status is null;
        alter table professor alter column status set not null;
    end if;
end $$;
