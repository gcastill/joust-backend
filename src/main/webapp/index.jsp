<html lang="en">
<head>
<meta name="google-signin-scope" content="profile email">
<meta name="google-signin-client_id"
	content="1085953861517-f98qb7d43294d9a0a809v6hf68iqliv9.apps.googleusercontent.com">
<script src="https://apis.google.com/js/platform.js" async defer></script>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
	integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
	crossorigin="anonymous" />

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
	integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r"
	crossorigin="anonymous" />

<link rel='stylesheet' href="css/joust.css" />
<title>Joust</title>
</head>

<body>
	<div id='login' class="container">
		<h2 class="form-signin-heading">Please sign into Google.</h2>
		<div id='google-login' class="g-signin2" data-onsuccess="onSignIn"
			data-theme="dark"></div>
	</div>

	<div id='info' class="container" style="display: none;">
		<div>
			<img id='img' alt="img" src="" />
		</div>
		<div>
			<span id='given-name'></span>
		</div>
		<div>
			<span id='family-name'></span>
		</div>
		<div>
			<span id='email'></span>
		</div>
		<button id='logout' type="button" class="btn btn-primary">Logout</button>

	</div>

	<script>
		function onSignIn(googleUser) {

			var idToken = googleUser.getAuthResponse().id_token;

			$.post("<%=request.getContextPath()%>/rest/google/verifyLogin", idToken);

			// Useful data for your client-side scripts:
			var profile = googleUser.getBasicProfile();

			$("#login").hide();
			$('#img').attr("src", profile.getImageUrl());
			$('#given-name').text(profile.getGivenName());
			$('#family-name').text(profile.getFamilyName());
			$('#email').text(profile.getEmail());
			$('#info').show();

		};
	</script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
		integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS"
		crossorigin="anonymous"></script>

	<script type="text/javascript">
		$(document).ready(function() {
			$("#logout").click(function() {
				gapi.auth2.getAuthInstance().signOut();
				$("#login").show();
				$("#info").hide();
			});
		});
	</script>
</body>
</html>