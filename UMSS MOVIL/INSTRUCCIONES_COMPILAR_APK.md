# 📱 Obtener el APK con GitHub Actions (sin instalar nada pesado)

## Lo que necesitas instalar (muy liviano)
- **Git**: https://git-scm.com/download/win → solo ~50MB
- **Cuenta GitHub**: https://github.com → gratis, solo registro web

---

## PASO 1 — Crear repositorio en GitHub

1. Entra a https://github.com y crea una cuenta (si no tienes)
2. Haz clic en el botón verde **"New"** (repositorio nuevo)
3. Nombre: `umss-horario`
4. Déjalo en **Public**
5. **NO** marques "Add README"
6. Clic en **"Create repository"**
7. Copia la URL que aparece, tipo: `https://github.com/TU_USUARIO/umss-horario.git`

---

## PASO 2 — Subir el código con Git

Abre **CMD** (Símbolo del sistema) en tu PC y ejecuta estos comandos uno por uno:

```
cd "C:\Users\POTER\Desktop\UMSS MOVIL"
git init
git add .
git commit -m "UMSS Horario app inicial"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/umss-horario.git
git push -u origin main
```

> ⚠️ Reemplaza `TU_USUARIO` con tu nombre de usuario de GitHub

---

## PASO 3 — GitHub compila el APK automáticamente

1. Ve a tu repositorio en GitHub
2. Haz clic en la pestaña **"Actions"**
3. Verás el workflow **"Build APK"** corriendo (círculo amarillo = compilando)
4. Espera ~5 minutos hasta que se ponga en verde ✅

---

## PASO 4 — Descargar el APK

1. En la pestaña **Actions**, haz clic en el workflow que terminó (verde ✅)
2. Baja hasta la sección **"Artifacts"**
3. Haz clic en **"UMSS-Horario-APK"** → se descarga un ZIP
4. Descomprime el ZIP → adentro está el archivo `app-debug.apk`

---

## PASO 5 — Instalar en tu celular Android

1. Pasa el APK a tu celular (WhatsApp, cable USB, Google Drive, etc.)
2. En el celular: **Ajustes → Seguridad → Instalar apps de fuentes desconocidas → Activar**
3. Abre el archivo APK desde el celular
4. Toca **"Instalar"**
5. ¡Listo! La app aparece como **"UMSS Horario"** 🎓

---

## ¿Problemas?

- Si el workflow falla (rojo ❌), haz clic en él para ver el error y dímelo
- El APK dura 30 días disponible para descarga en GitHub Actions
